package vf.arbiter.core.database.access.many.location

import utopia.vault.nosql.view.UnconditionalView

/**
  * The root access point when targeting multiple PostalCodes at a time
  * @author Mikko Hilpinen
  * @since 2021-10-14
  */
object DbPostalCodes extends ManyPostalCodesAccess with UnconditionalView

