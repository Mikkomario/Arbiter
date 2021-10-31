package vf.arbiter.core.database.access.many.company

import utopia.vault.nosql.view.UnconditionalView

/**
  * The root access point when targeting multiple Banks at a time
  * @author Mikko Hilpinen
  * @since 2021-10-14
  */
object DbBanks extends ManyBanksAccess with UnconditionalView

