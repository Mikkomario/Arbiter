package vf.arbiter.core.database.access.many.location

import utopia.vault.nosql.view.UnconditionalView

/**
  * The root access point when targeting multiple StreetAddresses at a time
  * @author Mikko Hilpinen
  * @since 2021-10-10
  */
object DbStreetAddresses extends ManyStreetAddressesAccess with UnconditionalView

