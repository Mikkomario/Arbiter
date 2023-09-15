# Arbiter Gold
Version: **v1.4**  
Updated: 2023-09-15

## Table of Contents
- [Enumerations](#enumerations)
  - [Currency](#currency)
  - [Metal](#metal)
  - [Weight Unit](#weight-unit)
- [Packages & Classes](#packages-and-classes)
  - [Price](#price)
    - [Metal Price](#metal-price)
  - [Settings](#settings)
    - [Common Setting](#common-setting)

## Enumerations
Below are listed all enumerations introduced in Arbiter Gold, in alphabetical order  

### Currency
Represents a monetary currency used in trading

Key: `id: Int`  
Default Value: **Euro**

**Values:**
- **Euro** (1) - Euro; Used in the European Union
- **Usd** (2) - US dollar; Used in the United States and in other countries as well

Utilized by the following 1 classes:
- [Metal Price](#metal-price)

### Metal
Represents a type of valuable metal used in trading

Key: `id: Int`  
Default Value: **Gold**

**Values:**
- **Gold** (1)
- **Silver** (2)

Utilized by the following 1 classes:
- [Metal Price](#metal-price)

### Weight Unit
Represents a unit used when measuring weights

Key: `id: Int`  

**Values:**
- **Gram** (1) - 1000th of a kilogram. Used in the metric system.
- **Kilogram** (2) - 1000 grams. Used in the metric system.
- **Troy Ounce** (3)

## Packages and Classes
Below are listed all classes introduced in Arbiter Gold, grouped by package and in alphabetical order.  
There are a total number of 2 packages and 2 classes

### Price
This package contains the following 1 classes: [Metal Price](#metal-price)

#### Metal Price
Documents a metal's (average) price on a specific date

##### Details
- Uses a **combo index**: `metal_id` => `currency_id` => `date`

##### Properties
Metal Price contains the following 4 properties:
- **Metal** - `metal: Metal` - Metal who's price is recorded
- **Currency** - `currency: Currency` - The currency in which the price is given
- **Date** - `date: LocalDate` - Date on which the price was used
- **Price Per Troy Ounce** - `pricePerTroyOunce: Double` - Price of the specified metal in the specified currency. Per one troy ounce of metal.

### Settings
This package contains the following 1 classes: [Common Setting](#common-setting)

#### Common Setting
Represents a single (mutable) setting key-value pair used in common configurations

##### Details
- **Chronologically** indexed
- Uses 2 database **indices**: `key`, `last_updated`

##### Properties
Common Setting contains the following 3 properties:
- **Key** - `key: String` - Key that represents this setting's target / function
- **Value** - `value: Value` - Value given for this setting
- **Last Updated** - `lastUpdated: Instant` - Time when this setting was last modified
