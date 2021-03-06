package vf.arbiter.core.model.stored.company

import utopia.vault.model.template.StoredModelConvertible
import vf.arbiter.core.database.access.single.company.DbSingleBank
import vf.arbiter.core.model.partial.company.BankData

/**
  * Represents a Bank that has already been stored in the database
  * @param id id of this Bank in the database
  * @param data Wrapped Bank data
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
case class Bank(id: Int, data: BankData) extends StoredModelConvertible[BankData]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this Bank in the database
	  */
	def access = DbSingleBank(id)
}

