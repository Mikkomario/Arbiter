package vf.arbiter.command.controller

import utopia.citadel.database.access.id.many.DbOrganizationIds
import utopia.citadel.database.access.many.description.{DbDescriptionRoles, DbOrganizationDescriptions}
import utopia.citadel.database.access.many.language.{DbLanguageFamiliarities, DbLanguages}
import utopia.citadel.database.access.many.user.{DbManyUserSettings, DbUserLanguageLinks}
import utopia.citadel.database.factory.organization.MembershipWithRolesFactory
import utopia.flow.datastructure.immutable.{Constant, Model, Value}
import utopia.flow.generic.ValueConversions._
import utopia.flow.util.CollectionExtensions._
import utopia.flow.util.FileExtensions._
import utopia.metropolis.model.combined.description.LinkedDescription
import utopia.metropolis.model.stored.description.DescriptionRole
import utopia.vault.database.Connection
import vf.arbiter.core.database.access.many.company.{DbBanks, DbCompanies, DbOrganizationCompanies}
import vf.arbiter.core.database.access.many.description.DbCompanyProductDescriptions
import vf.arbiter.core.database.access.many.invoice.{DbInvoiceItems, DbItemUnits}
import vf.arbiter.core.database.factory.company.{CompanyProductFactory, FullCompanyBankAccountFactory, FullCompanyDetailsFactory}
import vf.arbiter.core.database.factory.invoice.InvoiceFactory
import vf.arbiter.core.model.combined.company.FullCompanyBankAccount
import vf.arbiter.core.model.stored.company.Company

import java.nio.file.Path

/**
 * Used for exporting program data
 * @author Mikko Hilpinen
 * @since 29.10.2021, v1.1
 */
object ExportData
{
	/**
	 * Exports all data as a json document
	 * @param targetPath Path to the file that will be written
	 * @param connection Implicit DB Connection
	 * @return Path where the data was written. May contain a failure.
	 */
	def toJson(targetPath: Path)(implicit connection: Connection) =
	{
		val languages = DbLanguages.fullyDescribed
		val languageCodePerId = languages.map { l => l.id -> l.wrapped.isoCode }.toMap
		val descriptionRolePerId = DbDescriptionRoles.pull.map { r => r.id -> r }.toMap
		val companies = DbCompanies.pull
		val companyBankAccounts = FullCompanyBankAccountFactory.getAll()
		
		// Writes all data in groups
		val data = Model(Vector(
			"languages" -> languages.sortBy { _.wrapped.isoCode }.map { language => Model(Vector(
				"code" -> language.wrapped.isoCode,
				"descriptions" -> descriptionModelsFrom(language.descriptions, languageCodePerId, descriptionRolePerId)
			)) },
			"language_familiarities" -> DbLanguageFamiliarities.fullyDescribed.map { f => Model(Vector(
				"id" -> f.id,
				"order_index" -> f.wrapped.orderIndex,
				"descriptions" -> descriptionModelsFrom(f.descriptions, languageCodePerId, descriptionRolePerId)
			)) },
			"users" -> userModels(languageCodePerId),
			"units" -> unitModels(languageCodePerId, descriptionRolePerId),
			"banks" -> bankModels,
			"companies" -> companyModelsFrom(companies, companyBankAccounts.groupBy { _.companyId },
				languageCodePerId, descriptionRolePerId),
			"organizations" -> organizationModels(languageCodePerId, descriptionRolePerId,
				companies.map { c => c.id -> c.yCode }.toMap),
			"invoices" -> invoiceModels(languageCodePerId, companyBankAccounts.map { a => a.id -> a }.toMap)
		))
		targetPath.createParentDirectories().flatMap { _.writeJson(data) }
	}
	
	private def bankModels(implicit connection: Connection) =
		DbBanks.pull.sortBy { _.name }.map { _.toExportModel }
	
	private def userModels(languageCodePerId: Map[Int, String])(implicit connection: Connection) =
	{
		val languageLinksPerUserId = DbUserLanguageLinks.pull.groupBy { _.userId }
		DbManyUserSettings.pull.map { u =>
			Model(Vector("id" -> u.userId, "name" -> u.name,
				"languages" -> languageLinksPerUserId.getOrElse(u.id, Vector()).map { link =>
					Model(Vector("code" -> languageCodePerId(link.languageId), "familiarity_id" -> link.familiarityId))
				}))
		}
	}
	
	private def unitModels(languageCodePerId: Map[Int, String],
	                       descriptionRolePerId: Map[Int, DescriptionRole])(implicit connection: Connection) =
	{
		DbItemUnits.fullyDescribed
			.sortedWith(Ordering.by { _.wrapped.categoryId }, Ordering.by { _.wrapped.multiplier })
			.map { unit =>
				unit.toModel + Constant("descriptions", descriptionModelsFrom(unit.descriptions, languageCodePerId,
					descriptionRolePerId))
			}
	}
	
	private def companyModelsFrom(companies: Iterable[Company],
	                              bankAccountsPerCompanyId: Map[Int, Vector[FullCompanyBankAccount]],
	                              languageCodePerId: Map[Int, String], descriptionRolePerId: Map[Int, DescriptionRole])
	                             (implicit connection: Connection) =
	{
		val detailsPerCompanyId = FullCompanyDetailsFactory.getAll().groupBy { _.companyId }
		val productsPerCompanyId = CompanyProductFactory.getAll().groupBy { _.companyId }
		val productDescriptionsPerProductId = DbCompanyProductDescriptions.pull.groupBy { _.targetId }
		
		companies.toVector.sortBy { _.yCode }.map { company =>
			Model(Vector("y_code" -> company.yCode,
				"details" -> detailsPerCompanyId.getOrElse(company.id, Vector())
					.sortBy { _.created }.reverseIterator
					.map { _.toExportModel }.toVector,
				"products" -> productsPerCompanyId.getOrElse(company.id, Vector())
					.sortBy { _.created }.reverseIterator.map { product =>
					product.toExportModel +
						Constant("descriptions",
							descriptionModelsFrom(productDescriptionsPerProductId.getOrElse(product.id, Vector()),
								languageCodePerId, descriptionRolePerId))
				}.toVector,
				"bank_accounts" -> bankAccountsPerCompanyId.getOrElse(company.id, Vector())
					.sortBy { _.created }.reverseIterator.map { _.toExportModel }.toVector))
		}
	}
	
	private def organizationModels(languageCodePerId: Map[Int, String],
	                               descriptionRolePerId: Map[Int, DescriptionRole], companyYCodePerId: Map[Int, String])
	                              (implicit connection: Connection) =
	{
		val descriptionsPerOrganizationId = DbOrganizationDescriptions.all.groupBy { _.targetId }
		val membershipsPerOrganizationId = MembershipWithRolesFactory.getAll().groupBy { m => m.wrapped.organizationId }
		val companyYCodesPerOrganizationId = DbOrganizationCompanies.pull
			.groupMap { _.organizationId } { l => companyYCodePerId(l.companyId) }
		DbOrganizationIds.all.map { organizationId =>
			val companyCodes = companyYCodesPerOrganizationId.getOrElse(organizationId, Vector[String]())
			Model(Vector[(String, Value)](
				"id" -> organizationId,
				"descriptions" -> descriptionModelsFrom(descriptionsPerOrganizationId.getOrElse(organizationId, Vector()),
					languageCodePerId, descriptionRolePerId),
				"members" -> membershipsPerOrganizationId.getOrElse(organizationId, Vector()).map { membership =>
					Model(Vector("id" -> membership.wrapped.userId, "role_ids" -> membership.roleIds.toVector.sorted))
				},
				"company_codes" -> companyCodes
			))
		}
	}
	
	private def invoiceModels(languageCodePerId: Map[Int, String], bankAccountPerId: Map[Int, FullCompanyBankAccount])
	                         (implicit connection: Connection) =
	{
		val invoices = InvoiceFactory.getAll()
		val itemsPerInvoiceId = DbInvoiceItems.pull.groupBy { _.invoiceId }
		
		invoices.map { invoice =>
			Model(Vector(
				"id" -> invoice.id,
				"reference_code" -> invoice.referenceCode,
				"sender_details_id" -> invoice.senderCompanyDetailsId,
				"recipient_details_id" -> invoice.recipientCompanyDetailsId,
				"sender_bank_account" -> bankAccountPerId(invoice.senderBankAccountId).toExportModel,
				"language" -> languageCodePerId(invoice.languageId),
				"items" -> itemsPerInvoiceId.getOrElse(invoice.id, Vector()).map { _.toExportModel },
				"created" -> invoice.created,
				"product_delivery" -> invoice.productDeliveryDate,
				"payment_duration_days" -> invoice.paymentDuration.length,
				"cancelled_after" -> invoice.cancelledAfter
			))
		}
	}
	
	private def descriptionModelsFrom(descriptions: Iterable[LinkedDescription], languageCodePerId: Map[Int, String],
	                                  descriptionRolePerId: Map[Int, DescriptionRole]) =
		descriptions.groupBy { _.languageId }.toVector.sortBy { _._1 }.map { case (languageId, descriptions) =>
			Model(Vector("language" -> languageCodePerId(languageId),
				"descriptions" -> Model(descriptions.toVector.sortBy { _.roleId }.map { d =>
					descriptionRolePerId(d.roleId).jsonKeySingular -> (d.text: Value)
				})))
		}
}
