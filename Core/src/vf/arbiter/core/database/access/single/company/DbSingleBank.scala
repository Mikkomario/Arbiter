package vf.arbiter.core.database.access.single.company

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.arbiter.core.model.stored.company.Bank

/**
  * An access point to individual Banks, based on their id
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
case class DbSingleBank(id: Int) extends UniqueBankAccess with SingleIntIdModelAccess[Bank]

