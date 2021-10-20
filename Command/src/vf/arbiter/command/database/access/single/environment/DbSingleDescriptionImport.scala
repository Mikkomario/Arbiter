package vf.arbiter.command.database.access.single.environment

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.arbiter.command.model.stored.environment.DescriptionImport

/**
  * An access point to individual DescriptionImports, based on their id
  * @since 2021-10-20
  */
case class DbSingleDescriptionImport(id: Int) 
	extends UniqueDescriptionImportAccess with SingleIntIdModelAccess[DescriptionImport]

