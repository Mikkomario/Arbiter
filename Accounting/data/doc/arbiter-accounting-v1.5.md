# Arbiter Accounting
Version: **v1.5**  
Updated: 2024-01-04

## Table of Contents
- [Packages & Classes](#packages-and-classes)
  - [Account](#account)
    - [Account Balance](#account-balance)
  - [Target](#target)
    - [Allocation Target](#allocation-target)
    - [Type Specific Allocation Target](#type-specific-allocation-target)
  - [Transaction](#transaction)
    - [Invoice Payment](#invoice-payment)
    - [Party Entry](#party-entry)
    - [Transaction](#transaction)
    - [Transaction Evaluation](#transaction-evaluation)
    - [Transaction Type](#transaction-type)

## Packages and Classes
Below are listed all classes introduced in Arbiter Accounting, grouped by package and in alphabetical order.  
There are a total number of 3 packages and 8 classes

### Account
This package contains the following 1 classes: [Account Balance](#account-balance)

#### Account Balance
Represents a balance (monetary amount) on a bank account at a specific time

##### Details
- Fully **versioned**
- Uses 2 database **indices**: `created`, `deprecated_after`

##### Properties
Account Balance contains the following 5 properties:
- **Account Id** - `accountId: Int` - Id of the described bank account
  - Refers to *company_bank_account* from another module
- **Balance** - `balance: Double` - The amount of € on this account in this instance
- **Creator Id** - `creatorId: Option[Int]` - Id of the user who provided this information. None if not known or if not applicable.
  - Refers to *user* from another module
- **Created** - `created: Instant` - Time when this value was specified. Also represents the time when this value was accurate.
- **Deprecated After** - `deprecatedAfter: Option[Instant]` - Time when this statement was cancelled. None while valid.

### Target
This package contains the following 2 classes: [Allocation Target](#allocation-target), [Type Specific Allocation Target](#type-specific-allocation-target)

#### Allocation Target
Represents a goal or a target for money-allocation

##### Details
- Preserves **history**
- Uses a **combo index**: `deprecated_after` => `applied_until` => `applied_since`
- Uses **index**: `deprecated_after`

##### Properties
Allocation Target contains the following 7 properties:
- **Company Id** - `companyId: Int` - Id of the company for which these targets apply
  - Refers to *company* from another module
- **Capital Ratio** - `capitalRatio: Double` - The targeted (minimum) capital ratio of the income (possibly after certain expenses) [0,1].
- **Applied Since** - `appliedSince: Instant` - The first time from which this target is applied
- **Applied Until** - `appliedUntil: Option[Instant]` - Time until which this target was applied. None if applied indefinitely (or until changed).
- **Creator Id** - `creatorId: Option[Int]` - Id of the user who specified these targets
  - Refers to *user* from another module
- **Created** - `created: Instant` - Time when this target was specified
- **Deprecated After** - `deprecatedAfter: Option[Instant]` - Time when this target was cancelled / deprecated. Deprecated targets don't apply, not even in post.

##### Referenced from
- [Type Specific Allocation Target](#type-specific-allocation-target).`parentId`

#### Type Specific Allocation Target
Represents an allocation / target for a specific transaction-type

##### Details

##### Properties
Type Specific Allocation Target contains the following 4 properties:
- **Parent Id** - `parentId: Int` - Id of the target to which this specific value belongs
  - Refers to [Allocation Target](#allocation-target)
- **Type Id** - `typeId: Int` - Id of the allocated transaction type
  - Refers to [Transaction Type](#transaction-type)
- **Ratio** - `ratio: Double` - The targeted ratio of total after-expenses income that should be allocated into this transaction type. 
If a ratio has been specified for a parent transaction type, this ratio is applied to the parent's portion (E.g. ratio of 1.0 would allocate 100% of the parent's share to this child type). 
[0,1]
- **Is Maximum** - `isMaximum: Boolean` - Whether this target represents the largest allowed value. False if this represents the minimum target.

### Transaction
This package contains the following 5 classes: [Invoice Payment](#invoice-payment), [Party Entry](#party-entry), [Transaction](#transaction), [Transaction Evaluation](#transaction-evaluation), [Transaction Type](#transaction-type)

#### Invoice Payment
Links an invoice with the transaction where it was paid

##### Details

##### Properties
Invoice Payment contains the following 5 properties:
- **Invoice Id** - `invoiceId: Int` - Id of the paid invoice
  - Refers to *invoice* from another module
- **Transaction Id** - `transactionId: Int` - Id of the transaction that paid the linked invoice
  - Refers to [Transaction](#transaction)
- **Creator Id** - `creatorId: Option[Int]` - Id of the user who registered this connection
  - Refers to *user* from another module
- **Created** - `created: Instant` - Time when this link was registered
- **Manual** - `manual: Boolean` - Whether this connection was made manually (true), or whether it was determined by an automated algorithm (false)

#### Party Entry
Represents a transaction party as described on a bank statement

##### Details
- Uses **index**: `name`

##### Properties
Party Entry contains the following 2 properties:
- **Name** - `name: String` - Name of this entity, just as it appeared on a bank statement
- **Created** - `created: Instant` - Time when this party entry was added to the database

##### Referenced from
- [Transaction](#transaction).`otherPartyEntryId`

#### Transaction
Represents a transaction from or to a bank account. Only includes data from a bank statement.

##### Details
- Utilizes **localized descriptions**
- Preserves **history**
- Uses 2 database **indices**: `date`, `deprecated_after`

##### Properties
Transaction contains the following 9 properties:
- **Account Id** - `accountId: Int` - Id of the account on which this transaction was made
  - Refers to *company_bank_account* from another module
- **Date** - `date: LocalDate` - Date when this transaction occurred (e.g. date of purchase)
- **Record Date** - `recordDate: LocalDate` - Date when this transaction was recorded / actuated on the bank account.
- **Amount** - `amount: Double` - The size of the transaction in €. Positive amounts indicate balance added to the account. Negative values indicate withdrawals or purchases.
- **Other Party Entry Id** - `otherPartyEntryId: Int` - Id of the other party of this transaction, as it appears on this original statement.
  - Refers to [Party Entry](#party-entry)
- **Reference Code** - `referenceCode: String` - Reference code mentioned on the transaction. May be empty.
- **Creator Id** - `creatorId: Option[Int]` - Id of the user who added this entry. None if unknown or if not applicable.
  - Refers to *user* from another module
- **Created** - `created: Instant` - Time when this transaction was added to the database
- **Deprecated After** - `deprecatedAfter: Option[Instant]` - Time when this entry was cancelled / removed. None while valid.

##### Referenced from
- [Invoice Payment](#invoice-payment).`transactionId`
- [Transaction Evaluation](#transaction-evaluation).`transactionId`

#### Transaction Evaluation
Lists information that's added on top of a raw transaction record. Typically based on user input.

##### Details
- Preserves **history**
- Uses **index**: `deprecated_after`

##### Properties
Transaction Evaluation contains the following 8 properties:
- **Transaction Id** - `transactionId: Int` - Id of the described transaction.
  - Refers to [Transaction](#transaction)
- **Type Id** - `typeId: Int` - Id of the assigned type of this transaction
  - Refers to [Transaction Type](#transaction-type)
- **Vat Ratio** - `vatRatio: Double`, `0.0` by default - Ratio of VAT applied to this transaction, where 0.5 is 50% and 1.0 is 100%.
- **Other Party Alias** - `otherPartyAlias: String` - An alias given to the other party of this transaction. Empty if no alias has been specified.
- **Creator Id** - `creatorId: Option[Int]` - Id of the user who added this evaluation. None if unknown or if not applicable.
  - Refers to *user* from another module
- **Created** - `created: Instant` - Time when this transaction evaluation was added to the database
- **Deprecated After** - `deprecatedAfter: Option[Instant]` - Time when this evaluation was replaced or cancelled. None while valid.
- **Manual** - `manual: Boolean` - Whether this evaluation is manually performed by a human. False if performed by an algorithm.

#### Transaction Type
Represents a category or a type of transaction

##### Details
- Utilizes **localized descriptions**

##### Properties
Transaction Type contains the following 4 properties:
- **Parent Id** - `parentId: Option[Int]` - Id of the parent type of this type. None if this is a root/main category.
  - Refers to [Transaction Type](#transaction-type)
- **Creator Id** - `creatorId: Option[Int]` - Reference to the user that created this transaction type
  - Refers to *user* from another module
- **Created** - `created: Instant` - Time when this transaction type was added to the database
- **Pre Applied** - `preApplied: Boolean` - Whether these transaction types should be immediately as income or expense, before targets are applied. 
E.g. some expenses may be deducted from income instead of considered additional spending. 
Main input sources should also be pre-applied.

##### Referenced from
- [Transaction Evaluation](#transaction-evaluation).`typeId`
- [Transaction Type](#transaction-type).`parentId`
- [Type Specific Allocation Target](#type-specific-allocation-target).`typeId`
