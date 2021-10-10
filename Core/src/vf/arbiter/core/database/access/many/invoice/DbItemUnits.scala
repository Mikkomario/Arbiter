package vf.arbiter.core.database.access.many.invoice

import utopia.vault.nosql.view.UnconditionalView

/**
  * The root access point when targeting multiple ItemUnits at a time
  * @author Mikko Hilpinen
  * @since 2021-10-10
  */
object DbItemUnits extends ManyItemUnitsAccess with UnconditionalView

