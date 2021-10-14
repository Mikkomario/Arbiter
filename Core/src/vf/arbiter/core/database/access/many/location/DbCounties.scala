package vf.arbiter.core.database.access.many.location

import utopia.vault.nosql.view.UnconditionalView

/**
  * The root access point when targeting multiple Counties at a time
  * @author Mikko Hilpinen
  * @since 2021-10-14
  */
object DbCounties extends ManyCountiesAccess with UnconditionalView

