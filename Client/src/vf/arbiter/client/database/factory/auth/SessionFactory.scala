package vf.arbiter.client.database.factory.auth

import utopia.flow.generic.model.immutable.Model
import utopia.vault.nosql.factory.row.FromRowFactoryWithTimestamps
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import utopia.vault.nosql.template.Deprecatable
import vf.arbiter.client.database.ArbiterClientTables
import vf.arbiter.client.database.model.auth.SessionModel
import vf.arbiter.client.model.partial.auth.SessionData
import vf.arbiter.client.model.stored.auth.Session

/**
  * Used for reading session data from the DB
  * @author Mikko Hilpinen
  * @since 01.05.2023, v2.0
  */
object SessionFactory 
	extends FromValidatedRowModelFactory[Session] with FromRowFactoryWithTimestamps[Session] with Deprecatable
{
	// IMPLEMENTED	--------------------
	
	override def creationTimePropertyName = "created"
	
	override def nonDeprecatedCondition = SessionModel.nonDeprecatedCondition
	
	override def table = ArbiterClientTables.session
	
	override protected def fromValidatedModel(valid: Model) = 
		Session(valid("id").getInt, SessionData(valid("userId").getInt, valid("created").getInstant, 
			valid("deprecatedAfter").instant))
}

