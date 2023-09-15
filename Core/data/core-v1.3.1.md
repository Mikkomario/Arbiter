# Core
Version: **v1.3.1**  
Updated: 2023-09-15

## Table of Contents
- [Packages & Classes](#packages-and-classes)
  - [Company](#company)
    - [Bank](#bank)
    - [Company](#company)
    - [Company Bank Account](#company-bank-account)
    - [Company Details](#company-details)
    - [Company Product](#company-product)
    - [Organization Company](#organization-company)
  - [Invoice](#invoice)
    - [Invoice](#invoice)
    - [Invoice Item](#invoice-item)
    - [Invoice Payment](#invoice-payment)
    - [Item Unit](#item-unit)
    - [Unit Category](#unit-category)
  - [Location](#location)
    - [County](#county)
    - [Postal Code](#postal-code)
    - [Street Address](#street-address)

## Packages and Classes
Below are listed all classes introduced in Core, grouped by package and in alphabetical order.  
There are a total number of 3 packages and 14 classes

### Company
This package contains the following 6 classes: [Bank](#bank), [Company](#company), [Company Bank Account](#company-bank-account), [Company Details](#company-details), [Company Product](#company-product), [Organization Company](#organization-company)

#### Bank
Represents a bank (used with bank addresses)

##### Details
- Uses 2 database **indices**: `name`, `bic`

##### Properties
Bank contains the following 4 properties:
- **Name** - `name: String` - (Short) name of this bank
- **Bic** - `bic: String` - BIC-code of this bank
- **Creator Id** - `creatorId: Option[Int]` - Id of the user who registered this address
  - Refers to *user* from another module
- **Created** - `created: Instant` - Time when this bank was added to the database

##### Referenced from
- [Company Bank Account](#company-bank-account).`bankId`

#### Company
Represents a registered company (or an individual person)

##### Details
- Combines with [Company Details](#company-details), creating a **Detailed Company**
- **Chronologically** indexed
- Uses 2 database **indices**: `y_code`, `created`

##### Properties
Company contains the following 3 properties:
- **Y Code** - `yCode: String` - Official registration code of this company (id in the country system)
- **Creator Id** - `creatorId: Option[Int]` - Id of the user who registered this company to this system
  - Refers to *user* from another module
- **Created** - `created: Instant` - Time when this company was registered

##### Referenced from
- [Company Bank Account](#company-bank-account).`companyId`
- [Company Details](#company-details).`companyId`
- [Company Product](#company-product).`companyId`
- [Organization Company](#organization-company).`companyId`

#### Company Bank Account
Used for listing which bank accounts belong to which company

##### Details
- Combines with [Bank](#bank), creating a **Full Company Bank Account**
- Preserves **history**
- Uses **index**: `deprecated_after`

##### Properties
Company Bank Account contains the following 7 properties:
- **Company Id** - `companyId: Int` - Id of the company which owns this bank account
  - Refers to [Company](#company)
- **Bank Id** - `bankId: Int` - Id of the bank where the company owns an account
  - Refers to [Bank](#bank)
- **Address** - `address: String` - The linked bank account address
- **Creator Id** - `creatorId: Option[Int]` - Id of the user linked with this company bank account
  - Refers to *user* from another module
- **Created** - `created: Instant` - Time when this information was registered
- **Deprecated After** - `deprecatedAfter: Option[Instant]` - Time when this company bank account became deprecated. None while this company bank account is still valid.
- **Is Official** - `isOfficial: Boolean`, `false` by default - Whether this bank account information was written by the company authorities

##### Referenced from
- [Invoice](#invoice).`senderBankAccountId`

#### Company Details
Contains company information which may change and on which there may be varying views

##### Details
- Preserves **history**
- Uses 2 database **indices**: `name`, `deprecated_after`

##### Properties
Company Details contains the following 8 properties:
- **Company Id** - `companyId: Int` - Id of the company which this describes
  - Refers to [Company](#company)
- **Name** - `name: String` - Name of this company
- **Address Id** - `addressId: Int` - Street address of this company's headquarters or operation
  - Refers to [Street Address](#street-address)
- **Tax Code** - `taxCode: String` - Tax-related identifier code for this company
- **Creator Id** - `creatorId: Option[Int]` - Id of the user who wrote this description
  - Refers to *user* from another module
- **Created** - `created: Instant` - Time when this company details was added to the database
- **Deprecated After** - `deprecatedAfter: Option[Instant]` - Time when this company details became deprecated. None while this company details is still valid.
- **Is Official** - `isOfficial: Boolean`, `false` by default - Whether this information is by the company which is being described, having a more authority

##### Referenced from
- [Invoice](#invoice).`senderCompanyDetailsId`
- [Invoice](#invoice).`recipientCompanyDetailsId`

#### Company Product
Represents a type of product sold by an individual company

##### Details
- Utilizes **localized descriptions**
- Fully **versioned**
- Uses a **combo index**: `discontinued_after` => `created`
- Uses 2 database **indices**: `created`, `discontinued_after`

##### Properties
Company Product contains the following 7 properties:
- **Company Id** - `companyId: Int` - Id of the company that owns this product type
  - Refers to [Company](#company)
- **Unit Id** - `unitId: Int` - Id representing the units in which this product or service is sold
  - Refers to [Item Unit](#item-unit)
- **Default Unit Price** - `defaultUnitPrice: Option[Double]` - Default € price per single unit of this product
- **Tax Modifier** - `taxModifier: Double`, `0.24` by default - A modifier that is applied to this product's price to get the applied tax
- **Creator Id** - `creatorId: Option[Int]` - Id of the user linked with this company product
  - Refers to *user* from another module
- **Created** - `created: Instant` - Time when this product was registered
- **Discontinued After** - `discontinuedAfter: Option[Instant]` - Time when this product was discontinued (no longer sold)

##### Referenced from
- [Invoice Item](#invoice-item).`productId`

#### Organization Company
Connects organizations with their owned companies

##### Details

##### Properties
Organization Company contains the following 4 properties:
- **Organization Id** - `organizationId: Int` - Id of the owner organization
  - Refers to *organization* from another module
- **Company Id** - `companyId: Int` - Id of the owned company
  - Refers to [Company](#company)
- **Creator Id** - `creatorId: Option[Int]` - Id of the user who created this link
  - Refers to *user* from another module
- **Created** - `created: Instant` - Time when this organization company was added to the database

### Invoice
This package contains the following 5 classes: [Invoice](#invoice), [Invoice Item](#invoice-item), [Invoice Payment](#invoice-payment), [Item Unit](#item-unit), [Unit Category](#unit-category)

#### Invoice
Represents a bill / an invoice sent by one company to another to request a monetary transfer / payment

##### Details
- Combines with possibly multiple [Invoice Items](#invoice-item), creating a **Invoice With Items**
- May combine with [Invoice Payment](#invoice-payment), creating a **Invoice With Payment**
- Fully **versioned**
- Uses 3 database **indices**: `reference_code`, `created`, `cancelled_after`

##### Properties
Invoice contains the following 11 properties:
- **Sender Company Details Id** - `senderCompanyDetailsId: Int` - Id of the details of the company who sent this invoice (payment recipient)
  - Refers to [Company Details](#company-details)
- **Recipient Company Details Id** - `recipientCompanyDetailsId: Int` - Id of the details of the recipient company used in this invoice
  - Refers to [Company Details](#company-details)
- **Sender Bank Account Id** - `senderBankAccountId: Int` - Id of the bank account the invoice sender wants the recipient to transfer money to
  - Refers to [Company Bank Account](#company-bank-account)
- **Language Id** - `languageId: Int` - Id of the language used in this invoice
  - Refers to *language* from another module
- **Reference Code** - `referenceCode: String` - A custom reference code used by the sender to identify this invoice
- **Payment Duration** - `paymentDuration: Days`, `Days(30)` by default - Number of days during which this invoice can be paid before additional consequences
- **Product Delivery Begin** - `productDeliveryBegin: Option[LocalDate]` - The first date when the products were delivered, if applicable
- **Product Delivery End** - `productDeliveryEnd: Option[LocalDate]` - The last date when the invoiced products were delivered, if applicable
- **Creator Id** - `creatorId: Option[Int]` - Id of the user who created this invoice
  - Refers to *user* from another module
- **Created** - `created: Instant` - Time when this invoice was added to the database
- **Cancelled After** - `cancelledAfter: Option[Instant]` - Time when this invoice became deprecated. None while this invoice is still valid.

##### Referenced from
- [Invoice Item](#invoice-item).`invoiceId`
- [Invoice Payment](#invoice-payment).`invoiceId`

#### Invoice Item
Represents an individual sold item or service

##### Details

##### Properties
Invoice Item contains the following 5 properties:
- **Invoice Id** - `invoiceId: Int` - Id of the invoice on which this item appears
  - Refers to [Invoice](#invoice)
- **Product Id** - `productId: Int` - Id of the type of product this item represents / is
  - Refers to [Company Product](#company-product)
- **Description** - `description: String` - Name or description of this item (in the same language the invoice is given in)
- **Price Per Unit** - `pricePerUnit: Double` - Euro (€) price per each sold unit of this item, without taxes applied
- **Units Sold** - `unitsSold: Double`, `1.0` by default - Amount of items sold in the product's unit

#### Invoice Payment
Represents a payment event concerning an invoice you have sent

##### Details
- Uses **index**: `date`

##### Properties
Invoice Payment contains the following 5 properties:
- **Invoice Id** - `invoiceId: Int` - Id of the invoice that was paid
  - Refers to [Invoice](#invoice)
- **Date** - `date: LocalDate` - Date when this payment was received
- **Received Amount** - `receivedAmount: Double` - Received amount in €, including taxes
- **Remarks** - `remarks: String` - Free remarks concerning this payment
- **Created** - `created: Instant` - Time when this payment was registered

#### Item Unit
Represents a unit in which items can be counted

##### Details
- Utilizes **localized descriptions**

##### Properties
Item Unit contains the following 2 properties:
- **Category Id** - `categoryId: Int` - Id of the category this unit belongs to
  - Refers to [Unit Category](#unit-category)
- **Multiplier** - `multiplier: Double`, `1.0` by default - A multiplier that, when applied to this unit, makes it comparable with the other units in the same category

##### Referenced from
- [Company Product](#company-product).`unitId`

#### Unit Category
Represents different categories a unit can belong to. Units within a category can be compared.

##### Details
- Utilizes **localized descriptions**

##### Properties
Unit Category contains the following 0 properties:

##### Referenced from
- [Item Unit](#item-unit).`categoryId`

### Location
This package contains the following 3 classes: [County](#county), [Postal Code](#postal-code), [Street Address](#street-address)

#### County
Represents a county within a country

##### Details
- Uses **index**: `name`

##### Properties
County contains the following 3 properties:
- **Name** - `name: String` - County name, with that county's or country's primary language
- **Creator Id** - `creatorId: Option[Int]` - Id of the user who registered this county
  - Refers to *user* from another module
- **Created** - `created: Instant` - Time when this county was added to the database

##### Referenced from
- [Postal Code](#postal-code).`countyId`

#### Postal Code
Represents a postal code, which represents an area within a county

##### Details
- Combines with [County](#county), creating a **Full Postal Code**
- Uses **index**: `number`

##### Properties
Postal Code contains the following 4 properties:
- **Number** - `number: String` - The number portion of this postal code
- **County Id** - `countyId: Int` - Id of the county where this postal code is resides
  - Refers to [County](#county)
- **Creator Id** - `creatorId: Option[Int]` - Id of the user linked with this postal code
  - Refers to *user* from another module
- **Created** - `created: Instant` - Time when this postal code was added to the database

##### Referenced from
- [Street Address](#street-address).`postalCodeId`

#### Street Address
Represents a specific street address

##### Details
- Uses a **combo index**: `postal_code_id` => `street_name` => `building_number` => `stair` => `room_number`

##### Properties
Street Address contains the following 7 properties:
- **Postal Code Id** - `postalCodeId: Int` - Id of the postal code linked with this street address
  - Refers to [Postal Code](#postal-code)
- **Street Name** - `streetName: String` - Name of the street -portion of this address
- **Building Number** - `buildingNumber: String` - Number of the targeted building within the specified street
- **Stair** - `stair: String` - Number or letter of the targeted stair within that building, if applicable
- **Room Number** - `roomNumber: String` - Number of the targeted room within that stair / building, if applicable
- **Creator Id** - `creatorId: Option[Int]` - Id of the user who registered this address
  - Refers to *user* from another module
- **Created** - `created: Instant` - Time when this street address was added to the database

##### Referenced from
- [Company Details](#company-details).`addressId`
