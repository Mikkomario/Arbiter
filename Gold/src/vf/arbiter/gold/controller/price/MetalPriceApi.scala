package vf.arbiter.gold.controller.price

import utopia.access.http.{Headers, Status}
import utopia.annex.controller.{ApiClient, PreparingResponseParser}
import utopia.annex.model.response.Response
import utopia.annex.util.ResponseParseExtensions._
import utopia.bunnymunch.jawn.JsonBunny
import utopia.disciple.apache.Gateway
import utopia.disciple.controller.RequestInterceptor
import utopia.disciple.http.request.{Body, Request, StringBody}
import utopia.disciple.http.response.ResponseParser
import utopia.flow.collection.immutable.Empty
import utopia.flow.collection.immutable.caching.cache.Cache
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.{Constant, Model, Value}
import utopia.flow.parse.json.JsonParser
import utopia.flow.time.DateRange
import utopia.flow.util.TryExtensions._
import utopia.flow.util.logging.Logger
import vf.arbiter.core.util.Common
import vf.arbiter.core.util.Common.executionContext
import vf.arbiter.gold.controller.price.MetalPriceApi.{InsertApiKeyInterceptor, parseFailureStatus}
import vf.arbiter.gold.model.cached.auth.ApiKey
import vf.arbiter.gold.model.enumeration.{Currency, Metal}
import vf.arbiter.gold.model.partial.price.MetalPriceData

import scala.annotation.unused
import scala.concurrent.ExecutionContext
import scala.language.implicitConversions
import scala.util.{Failure, Success}

object MetalPriceApi
{
	// ATTRIBUTES   -----------------------
	
	private val cache = Cache { apiKey: String => new MetalPriceApi(apiKey) }
	
	private val parseFailureStatus = new Status("Response-parsing failed", 599, isTemporary = false, doNotRepeat = true)
	
	
	// INITIAL CODE -----------------------
	
	Status.introduce(parseFailureStatus)
	
	
	// IMPLICIT ---------------------------
	
	implicit def usingImplicitKey(@unused a: MetalPriceApi.type)(implicit key: ApiKey): MetalPriceApi = using(key.key)
	
	
	// OTHER    ---------------------------
	
	/**
	 * @param apiKey API-key to use for authorizing the requests
	 * @return API-access that uses that key
	 */
	def using(apiKey: String) = cache(apiKey)
	
	
	// NESTED   ---------------------------
	
	private class InsertApiKeyInterceptor(apiKey: String) extends RequestInterceptor
	{
		override def intercept(request: Request): Request =
			request.copy(params = request.params + Constant("api_key", apiKey))
	}
}

/**
 * Interface used for making metal price requests
 * @author Mikko Hilpinen
 * @since 14.9.2023, v1.4
 */
// TODO: Rename to MetalPriceApiClient
class MetalPriceApi(apiKey: String) extends ApiClient
{
	// ATTRIBUTES   ---------------------
	
	override protected lazy val gateway: Gateway = Gateway(
		requestInterceptors = Vector(new InsertApiKeyInterceptor(apiKey)),
		allowJsonInUriParameters = false, allowBodyParameters = false)
	override protected lazy val rootPath: String = "https://api.metalpriceapi.com/v1"
	
	override val valueResponseParser: ResponseParser[Response[Value]] =
		ResponseParser.value.unwrapToResponse(parseFailureStatus) { _.getString }
	override val emptyResponseParser: ResponseParser[Response[Unit]] =
		PreparingResponseParser.onlyRecordFailures(ResponseParser.value.map {
			case Success(body) => body.getString
			case Failure(error) =>
				log(error, "Failed to parse a response body")
				"Failed to parse the response body"
		})
	
	
	// IMPLEMENTED  ---------------------
	
	override protected def exc: ExecutionContext = executionContext
	override protected implicit def jsonParser: JsonParser = JsonBunny
	override protected implicit def log: Logger = Common.log
	
	override protected def responseParseFailureStatus: Status = parseFailureStatus
	
	override protected def modifyOutgoingHeaders(original: Headers): Headers = original
	override protected def makeRequestBody(bodyContent: Value): Body = StringBody.json(bodyContent.getString)
	
	
	// OTHER    -------------------------
	
	/**
	 * Requests metal prices during a specific date range
	 * @param metal Targeted metal
	 * @param to Currency in which the price should be listed
	 * @param during Dates during which the price should be listed (max 365 days)
	 * @return Future that resolves into parsed metal price entries, or to a failure
	 */
	// TODO: Handle case where the date range only covers a single date
	// TODO: Current version fails if during > 365 days
	def pricesDuring(metal: Metal, to: Currency, during: DateRange) = {
		// Requests the prices on the targeted date range
		get("timeframe", params = Model.from(
			"start_date" -> during.start, "end_date" -> during.last,
			"base" -> metal.code, "currencies" -> to.code))
			.getModel
			// Processes the results once they arrive
			.map {
				// Case: Success => Parses price data from the response body, if possible
				case Response.Success(body, _, _) =>
					// Expects the response to contain a "rates" property that contains
					// dates as keys and price objects as values
					body("rates").getModel.properties.map { prop =>
						prop.name.tryLocalDate.flatMap { date =>
							// Each price object is expected to specify the price using currency codes as property keys
							prop.value(to.code).tryDouble.map { price =>
								MetalPriceData(metal, to, date, price)
							}
						}
					}.toTryCatch
				// Case: Failure
				case r => r.toTry.map { _ => Empty }.toTryCatch
			}
	}
}
