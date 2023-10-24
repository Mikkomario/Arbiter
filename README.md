# Arbiter

## What is Arbiter?
Arbiter is a command line application used in invoicing. 

Arbiter stores all your invoicing data in a local database and utilizes that data in the invoice-creation process. 
For example, instead of filling customer details every time for every invoice, 
Arbiter automatically inserts this information based on your customer selection. Arbiter also generates 
reference codes and invoice numbers for you and handles VAT calculations. Basically this means that when you write 
an invoice, you get to focus on the important and changing matters: How much was sold and to whom.

Here are the main benefits of using Arbiter:
1. It makes **creating invoices faster and less tedious**
2. It **reduces the amount of human errors** in the invoicing process
3. It **handles VAT calculations** for you, making it much easier to track your VAT status
4. You can **export your invoicing data** in a human- and computer-readable format, so that it is available to you for your accounting
5. It gives you a nice (customizable) **invoice template**, improving the look-and-feel of your company brand

Additionally, Arbiter comes with a nifty set of tools for **gold-based inflation-correction**.  
See the section on [Arbiter and gold](#arbiter-and-gold) for more details about these features.

## Table of contents
This document contains the following sections:
1. **[What is Arbiter](#what-is-arbiter)** - a quick summary on what this software does and why you might want to use it
2. **[How to install Arbiter](#how-to-install-arbiter)**
3. **[How to start Arbiter](#how-to-start-arbiter)** - Step-by-step guide on how to run the application from the command line
4. **[Using Arbiter for the first time](#using-arbiter-for-the-first-time)** - Step-by-step guide on setting up your company data and writing your first invoice
5. **[Available commands](#available-commands)** - Describes the commands that are at your disposal when using this software
6. **[Creating a custom invoice form](#creating-a-custom-invoice-form)** - A guide on how to modify the default invoice template file
7. **[Arbiter and gold](#arbiter-and-gold)** - Describes Arbiter's gold-related features and their philosophical grounding
8. **[Feature-requests and other opportunities](#feature-requests-and-other-opportunities)** - Suggestions on how you can interact and contribute on this project
9. **[License and user agreement](#license-and-user-agreement)** - **Downloading, using and/or distributing this software places you under these terms** (but don't worry, they're not grievous)

## How to install Arbiter?
First, go to the **latest release** on the [Arbiter/Releases](https://github.com/Mikkomario/Arbiter/releases) page on GitHub. 
Find the **Assets**, header and **download the .7z file** locally on your computer.

Extract the .7z file to a directory from where you want to run this software. 
I recommend placing the directory in such a way that it is convenient to reach 
through the command line (Windows) or Terminal (Mac).  
If you don't know how to extract .7z files, please download [7-Zip](https://7-zip.org/) first.

Before running Arbiter, you need **Java JRE 8** or later installed on your device. 
If you don't have Java yet, please download and install the latest Java JRE 8 version from 
[OpenJDK](https://www.openlogic.com/openjdk-downloads?field_java_parent_version_target_id=416&field_operating_system_target_id=All&field_architecture_target_id=All&field_java_package_target_id=401).  
Arbiter is built and tested with JDK 8, so you may find the most reliable results using that version. 
But since Java is backwards-compatible, you should be able to use any later version just as well.

Once you have Java installed and have extracted the .7z file, you're ready to use Arbiter from the 
command line / Terminal.

## How to start Arbiter?
In this section, we will cover how to start the Arbiter software from the command line. 
We will not assume you're a technical wizard and know everything about command line applications, 
but we do assume that you're able to look things up when necessary.

### Windows users
Open the **CMD** application on your Windows computer. 
You can easily find the application with the main search feature by typing in `CMD`.

Go to the directory you extracted from the .7z file using the `cd` command. 
E.g. by typing `cd C:\Users\<you>\apps\Arbiter` and by pressing enter 
(you obviously have to customize the path, based on where you stored the file). 

Use the `dir` command to make sure you're in the right directory. You know you're in the right directory 
when you see `Arbiter.jar` as one of the listed files.

Use `java -jar Arbiter.jar` command to start the application.

### Mac users
Open the **Terminal.app** on your Mac. If you don't know how to do that, please check 
[this guide](https://www.howtogeek.com/682770/how-to-open-the-terminal-on-a-mac/).

Go to the directory you extracted from the .7z file using the `cd` command. 
If you're unfamiliar with the `cd` command, please check 
[this StackOverflow page](https://stackoverflow.com/questions/9547730/how-to-navigate-to-to-different-directories-in-the-terminal-mac).

Use the `ls` command to make sure you're in the right directory. 
You know you're in the right directory when you see `Arbiter.jar` in the list of files.

Start the Arbiter application by running `java -jar Arbiter.jar` command.

### Linux users
Go to the directory you extracted from the .7z file using the `cd` command and run the application using 
`java -jar Arbiter.jar` command.

## Using Arbiter for the first time
In this section we will cover the steps you need to take when first using the application. 
You will be adding your "user" and company information, and write your first invoice.

### Registering your own company
Start by calling the `register user <your name>` command. Replace `<your name>` with your first name, or whatever name 
you want to use to identify you within this application.

The application will next request you to specify the languages you wish to use. 
Write your list of languages using their matching 
**two-letter** [ISO 639-1 -codes](https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes) and then press enter. 
For example, your input might be: `en, fi`. It doesn't matter what you use to separate the entries, as long as you 
separate them with a non-letter.

Please note that **currently the application itself is available only in English (en)**, whereas **invoicing templates 
are only available in Finnish (fi)**. I may write an English invoicing template and/or a Finnish application version 
on request, if you contact me via email. For other languages, 
please collaborate with me to write the missing translations.

Next, the application asks you for the name of your company. Please write the whole name of your company, 
as you want it to appear within the invoices you write.

When the application asks whether you want to add a new company, select `yes` by typing `y` and pressing enter.

Next, specify the information the application requests from you:
1. Language in which your company name is given. E.g. `en`.
2. Company code. E.g. `3240693-7`.
3. County. E.g. `Helsinki`
4. Postal code. E.g. `00102`
5. Street name. E.g. `Mannerheimintie`
6. Building number. E.g. `1`
7. Stair, if applicable. E.g. `A`
8. Room number, if applicable. E.g. `12`
9. VAT code, which in Finland is FI plus your company code, without any separating characters included.

You're now ready to write invoices on you company's behalf.  
Later, when you start the application again, you may simply use `login <your name>` command to apply this information.

If you operate multiple companies, you may use the `register company <your other company name>` command to 
register additional companies. In these cases, follow the `login <your name>` command with 
`use <your company name>` command. You may use freely use partial company names in searches, like this one.

### Registering your first customer
Use the `register customer <customer company name>` command to register your first customer. 
The application will request the same information as it did with your own company. 
VAT code is not necessary in this case, as it is not printed on the generated invoices, 
but you are free to specify it nonetheless, if you want to.

### Writing your first invoice
Use the `invoice` command to start the invoice-writing process.

The application will first ask you for the name of the customer you're writing the invoice for. 
If you're targeting a customer you have already registered (like in this case), you may simply 
write a part of that customer's name.

Next, select the language in which this invoice is given. 
Remember that **right now only Finnish invoice templates are available, so I highly recommend selecting fi**.

Next, the application will request you for the allowed payment duration for the customer. 
This value depends on your contract with your customer. The typical values are 7, 14, 21 or 30, where 
30 is a safe option in case you don't have a clear contract. Obviously specifying a smaller value will typically 
correlate with a faster payment. Use discretion.

Next, the application will request you for the date or time period, on which the services were provided or the 
goods sold. Here you have multiple options: For example, if you're writing a monthly service invoice, write `last month`. 
If you provided the services on a specific date, write the date, such as `13.4.2023`. You may also specify a custom 
date range such as `12-16.4.2023` or `15.4-15.5.2023`. As you can see, you may omit repeating information, 
such as months or years, within date ranges. If you don't want to specify any date or date range, leave this field empty.

Depending on your choice of language, the application may request you to specify the names of certain 
units and their abbreviations in your selected language. Please write them as you want them to appear on the invoice.

Next, the application will request you to write specific invoice items. You may include one or more items, 
but please note that the **current invoice template only has room for 7 items**.

For each invoice item, the application will ask you to specify the following information:
1. The **product** this item represents
2. A specific name or **description** of this invoice item, as it shall appear on the invoice
3. **Number of units sold**, where the unit depends on the product you chose
4. The item **price for each unit** sold in **Euros**

For example, you may specify 3.5 hours of consulting with a specific description of 
"Technical consulting on invoicing products", where the price is 550 €/h. This would result in a total of 
1925 € charged, plus VAT.

You may then repeat this process for each remaining invoice item.

**Right now, the only supported currency is Euro**. If you need support for other currencies, 
please contact me via email, and I may add custom currency support.

Here, as this is your first invoice, you will need to create a new product.  
For this, the application will request you the following information:
1. Product **name** in the selected language
2. The **unit** used for this product (e.g. hours or pieces)
3. **Default product price** in Euros (optional)
4. **VAT percentage** applied to this product (e.g. 24)

If you're unsure about the VAT percentage you should apply, and you're a company operating in Finland, 
you're most likely required to charge 24% VAT. 
For Finnish regulations on the matter, please check 
[Verohallinto's page on VAT rates](https://www.vero.fi/yritykset-ja-yhteisot/verot-ja-maksut/arvonlisaverotus/arvonlisaveroprosentit/).

Next, because this is your first invoice, the application will request you to specify your bank account information 
in order for your customer to know where to pay for the services rendered.  
For this, you will need to specify:
1. **Name of your bank** - This information is used by you only
2. **BIC**, which is specific to your bank of choice
3. **IBAN** (e.g. FI 1234 5678 1234 90)

If you're unsure about your BIC code, just search "<bank name> BIC" using your preferred search engine.

Next, the application will ask whether you want to print the invoice into a PDF. Select `yes`.

Again, as this is your first time using the application, it will ask you to specify where the invoicing template 
file may be found. The default invoice template is found at `data/invoice-forms/laskutuspohja-v2.pdf`.

The application will then open the invoice pdf on your default pdf-opening application (typically your browser). 
The invoice is opened in an editable format, where you may still modify the information, as needed.  
Because of the limitations of the command line / terminal itself, you may have to fix non-ASCII characters, 
such as Ä and Ö by hand 😦 (if this gets really annoying for you, please contact me, and I'll what I can do about it). 
In the default invoice template, there's also a **small free text area** where you may write a custom message for your 
customer. Use this field to improve the customer's invoice-paying experience, if you want to.

Once you're done with finalizing the invoice, **save your changes to that same file** and close the pdf. 
Then, select `yes` when the application asks whether you want to "flatten" the pdf so that it's no longer editable.  
The application will then open the directory where your flattened pdf file resides. 
You're now free to move the file where you want to store it. Remember to send a copy to your customer, and one to 
your accountant (if applicable).

As you can see, the first time using this application, you have to input a lot of data. 
However, the next time you write an invoice, the process gets a lot more streamlined. 
This is because Arbiter will insert customer, product and bank information automatically for you, 
based on your earlier input. In cases where you need to edit this information, use the `edit` command.

Now, before you close the program, try using the `summary` command. After selecting the default options, 
the application should open a directory where you can find .csv files that contain information about your recent invoice. 
I personally find the `totals-monthly.csv` file the most useful, as it lists total invoicing amount before and after taxes. 
You're free to use this invoicing in your personal company accounting, or to send it to your accountant, 
in case they find it useful.

You have now successfully written your first invoice. When you need to write your next invoice, just use the 
`invoice` command again. For other use-cases, please check the [list of commands](#available-commands) below.

If you want to close the application for now, simply use the `exit` command.

## Available commands
In this section, we will list and describe the commands that are available to you when using Arbiter.

### Basic commands
The most basic commands you most often need are:
- `exit`, to close the application
- `help`, to list or describe the available commands
- `login <your name>` to "log in" when you first open the application
- `register customer <customer name>`
- `invoice` to write a new invoice
- `summary` to generate .csv files based on your invoicing data

`login`, `invoice`, `register` and `summary` commands we have already covered in the 
[Using Arbiter for the first time](#using-arbiter-for-the-first-time) -section.  

`help` command is available in two modes:
1. When used **without arguments**, the command lists available commands and their function
2. When specifying the name of the command as the first parameter (e.g. `help invoice`), 
  the help command will list more detailed information about the selected command.

The `register` command also allows you to register alternative owned companies and user accounts, if you so desire. 
Use the `help` command to get more information about those use-cases.

### Commands for reviewing, adding and editing information
These commands you will need from time to time in order to review, edit or correct information you've specified:
- `see company <company name part>` to list company information
- `see invoice <invoice reference>` to describe an earlier invoice
- `list invoices <customer name part> <time period>` to list invoices concerning a specific customer (or to list all invoices)
- `edit <mode> <target>` to edit an invoice, a company or a product
- `cancel <invoice reference>` to cancel an existing invoice

`see` command lists information concerning a specific company or an invoice. 
Use this command, for example, when you want to make sure you've updated a company's address correctly.

NB: The `<invoice reference>` part of these commands may be listed in three different modes:
1. **Invoice index** / **number**
2. **Reference code**
3. Full or partial **customer name**

If you have your pdf invoice available, you may check the matching invoice number or reference code from there.
In cases where you need to target the latest invoice for a specific customer, the easiest way is to use the
customer name as the invoice reference. In situations where you've lost the pdf or don't want to search for it,
use the `list invoices <customer> <time period>` command to find the index of the invoice you want to target.

`edit company <company name part>` allows you to change information concerning your own company or concerning 
your customer. You will need this, for example, when you change your company's address. 
The process is quite straightforward.

`cancel <invoice reference>` allows you to cancel an existing invoice, 
although oftentimes `edit invoice <invoice reference>` is more appropriate. 
Cancelling an invoice will cause the invoice to not be listed anywhere, including the generated summary documents, 
as if you had never written the invoice in the first place. Obviously you still need to notify the customer, 
in case you had already sent the invoice to them.

`edit invoice <invoice reference>` will guide you through the invoice creation process with the previously inserted 
information as the default options. This is useful in case you inserted incorrect information to your invoice. 
The information you will choose to overwrite will not appear within the summaries or printed invoices after you've 
completed the changes. You will typically need to follow the edit invoice command 
with the `print <invoice reference>` command.

### Situational commands
You may need these commands in very specific circumstances:
- `print <invoice reference>` prints a new pdf copy of the selected invoice
- `use <company name part>` changes the company you're currently operating
- `claim <company name part>` converts a customer company into a company operated by you
- `backup` writes all saved data to a json file, which you may import to another Arbiter instance in order to use the same data-set
- `import <from>` command restores data from a backup json file
- `clear` command **deletes all (!) local data** - **Use with extreme caution**

`print` command is useful in situations where you've edited invoice information after you've created it.

`use` and `claim` commands are necessary only in situations where you intend to operate multiple companies. 
That is, if you want to write invoices in the name of multiple separate companies.

`backup` and `import` commands are useful in situations where you want to take a backup or to move the application to a 
new environment, although I recommend you to simply zip and copy the whole application directory, 
rather than using these commands. The `clear` command is related to `backup` and `import`, allowing you to make sure 
your result matches your backup exactly. However, please use these commands with extreme caution, as **their 
consequences typically cannot be reversed**.

## Creating a custom invoice form
You will find an .odt invoice template file from the `data/invoice-forms` directory. 
By modifying this document using [LibreOffice](https://www.libreoffice.org/), and then exporting the document to a 
pdf form, you may create a custom template for your personal use or redistribution.

Check [this tutorial](https://www.linuxuprising.com/2019/02/how-to-create-fillable-pdf-forms-with.html) on how to use 
LibreOffice to create pdf forms. Please note that **you must preserve the form field names** in order for Arbiter to 
recognize them correctly. But as long as you don't change the form field names, 
you should be free to modify this template.

## Arbiter and gold
In addition to invoicing features, Arbiter contains certain commands for setting and adjusting prices based on 
gold instead of Euro. While this is a niche use-case, it may benefit your company to review this section in order 
to understand, why using gold-based prices might be a good idea.

**Notice:** This section is based on my personal views. Going about speaking this information without 
verifying it first may easily get you labeled as a conspiracy theorist. 
I recommend researching the matter and exercising your critical thinking before 
trying to convince anyone else of these matters.

### The problem with Euro and USD
Euro and USD have a certain **major benefit** over non-traditional currencies: 
They're well marketed and declared [legal tender](https://www.investopedia.com/terms/l/legal-tender.asp), 
and are therefore, for the time being, largely accepted 
at their face value as money - meaning that people will **trade things of value** for these 
pieces of paper / numbers on a database. Another great benefit of using Euro and USD is that our governments 
don't accept any non-standard currency as payment for taxes or other fees. This means that people are **forced to use** 
these currencies as long as they're subject to our modern governments.

However, there are a **number of problems** involved when using these currencies:
1. Central banks may relatively easily, by conjuring more money out of debt, **destroy the value of your 
  holdings and your contracts** that are made in these currencies. For example, in 2020 the central banks 
  practically doubled the money supply. This means that every single Euro you owned before 2020 became half as valuable 
  **without you doing anything**, and **without most people even knowing what's going on**. 
  This process is called [inflation](#the-system-of-inflation), 
  although that word has been mislabeled on purpose by the governments, the media and the central banks.
2. These monetary units have **near-zero intrinsic value**. This means that, if and when your government decides to 
  discontinue the use of these currencies, the value of every Euro, USD etc. fiat instance you hold will "magically" 
  lose its value in the markets, and **you're left with nothing**. Typically, the authorities will give 
  you a **marginal compensation** for the currency you hold, but in practice this represents only a **fraction** of the 
  purchasing power these monetary units had before they became invalid.
3. Because these currencies are **consistently losing their purchasing power** through the process of inflation, 
  you either need to **continually increase your prices or to settle with less**. If and when you choose the first option, 
  your customers, who typically don't understand what inflation really is, incorrectly perceive you as a "greedy 
  capitalist" who "just wants more money". This has an **eroding effect on your customer-relationships**.

### The system of inflation
There are certain very critical problems facing us when it comes to inflation:
1. People **don't generally understand what inflation is**
2. People have been indoctrinated with a **false understanding** of what inflation is
3. Because people don't understand inflation, they don't even realize that they're **constantly being plundered**
4. Because people don't realize they're being plundered, 
  they're happy to **feed and to defend the monster** that's plundering them
5. Because people feed and defend the monster, it stays alive and **harms** even those who don't want to live with a monster

"What monster? I don't see any monsters around? For a software developer your mind seems to be a little bit loose. 
What kind of credentials do you have for speaking out on monetary policy?"

I know, I know. Depending on where you come from, this may sound a little strange to you. And don't worry, 
you can very easily stop reading here and just use Arbiter for invoicing and live your life in relative happiness. 
You don't need to agree with me. But, if you're interested in what I perceive as truth, and are ready to challenge 
some ideas you've been taught to not challenge, read on.

PS: With the monster I mean the man-made god known as the state

#### What's the fake definition of inflation we've been fed?
Here's an excerpt on the [Wikipedia article on inflation](https://en.wikipedia.org/wiki/Inflation):  
"In economics, inflation is a general increase of the prices of goods and services in an economy. 
This is usually measured using the consumer price index. 
When the general price level rises, each unit of currency buys fewer goods and services; 
consequently, inflation corresponds to a reduction in the purchasing power of money."

In this article, inflation is defined as the "general increase in the prices of goods and services". 
The causes of inflation, then, are described with very complex wording and basically reduced to the factors of 
supply, demand and expectation. Basically, the concept of inflation is **shrouded in great mystery**; Inflation 
is marketed as a concept that can only properly be understood by monetary experts and policy-makers, and is 
far beyond the understanding of an ordinary peasant (read: the taxpayer).

Everyone knows that inflation exists: they perceive the prices going up and have been given a label for this phenomenon. 
Like with the great magical force of gravity, we're fed with a label, plus a mathematical formula, 
to describe a perceived effect; 
We think we know what the effect is because we've been given a nice label with an interesting name on it. 
However, and I challenge you, talk with any random person on the supermarket and ask them what inflation is. 
The **best** you can expect to hear is "increase of prices". No actual understanding on the matter shall be 
demonstrated, I bet.

It is so very convenient for the authorities that the general population **doesn't even have a realistic definition 
of inflation**, even less an actual understanding of it. Why is it so convenient? Let me give you a hint, which 
comes from the article we referenced earlier: Inflation "is usually **measured using the consumer price index**".

#### Consumer price index
What's consumer price index (CPI), then? It is a **magic number** generated by a **closed-source algorithm** that's 100% 
controlled by the central banks; You can't see it, you can't test it, and you can't verify it - you just have to 
**trust it**.

What's so problematic of having some closed-source (magic) number used as a measurement of inflation? Well, 
the central banks report CPIs once a year (plus once per quarter, with seasonal adjustments). Because CPIs 
are the "single source of truth" when it comes to measuring inflation, and because nobody can actually verify these 
numbers, **people tend to accept them at their face value**. And what happens then? The governments automatically 
adjust their pensions, salaries, fees, etc. according to the released CPIs, and so do most companies. 
Adjusting the CPI slightly would then have quite **interesting effects** on the markets. 
Could somebody use that power to manipulate the markets? Someone like the central banks themselves? 
And who would know? Not the consumers, they don't even understand what 
inflation is. Not even the economists and experts could discern this sleight of hand, because they don't have 
access to the source code. Those who would have access to this information would surely be "well compensated" for 
their discretion on the matter.

And not only this, but **CPI practically dictates the perceived rate of inflation** for the great majority of people. 
What if the perceived rate of inflation was, let's say, 25%? That could cause some "political instability". 
Surely it would be in everyone's best interest to "adjust the algorithm" a little to give the uneducated population
(and especially their governing bodies) a little peace of mind, wouldn't it? But I'm sure our non-elected rulers 
wouldn't ever present false information to us, right? right?

And what if the process of inflation could be controlled in such a way that would **extract monetary power** from the 
unsuspecting population into the hands of those controlling that process? Wouldn't it be extremely convenient if one could 
also control the perceived rate of inflation to amplify the effect and to keep the population pacified? 
It would, wouldn't it? But such an atrocity would be an outlandish conspiracy, 
and nothing we need to bother ourselves thinking about. After all, you still need to go to work tomorrow.

As an interesting coincidence, as certain unofficial parties have been measuring the actual increase of prices 
in the United States, their numbers range between 8-20%, which is "slightly" above the CPIs reported by 
the Federal Reserve System. If you're interested, take a look at the [Chapwood index](https://chapwoodindex.com/).

#### What do I claim inflation is?
Here's my proposal on what inflation is in practical reality: Inflation is a **process, in which a major portion of 
the population's purchasing power is extracted** (especially from those who are weak, poor and ignorant enough not 
to be able to defend themselves), and then **redistributed** by an elite ruling class to themselves and to 
those who are in their "good favor".

I propose that the **main control-mechanisms** (i.e. parameters) of this function are:
- The **rate at which money is conjured** by the central banks
- Monetary policies which dictate how much money is conjured by the non-central banks
- The amount of **debt** governments are willing to take
- The published **CPI**

Other, very critical control-measures include:
- Public **education programs** that ensure the population attains a false understanding 
  of inflation and monetary systems in general 
- A system of educational and political **classes** that ensures that those not in the favor of the elite **do not receive 
  opportunities** that could challenge the current **monopoly**
- **Media campaigns** that enforce that false understanding and mystify the matters, so that they're not pursued by 
  those whose attention on the matter would "not be desirable"

Here's my proposed "pseudocode" on **how this function works**:
1. A central bank makes a contract with a government, in which they receive **perpetual interest** and the 
  government receives a **large quantity of newly conjured money**
2. The government in question **uses that money to advance its interests** 
  (typically to implement more authoritarian policies and to acquire greater monopolies in the market). 
  The perspective of the central banks is, naturally, held in high regard in this process, as fostering good relations 
  helps in keeping the future loans coming in.
3. The first recipients of the fresh money are **government officials** as well as **selected companies and social 
  classes** that are the beneficiaries of the governments "welfare policies".
4. The rest of the companies and people get to "earn" their share of the new money by trading it with their 
  labor and tangible assets.

The **output** of this function, when operated discreetly, is:
- The central banks gain a **compounding and infinite source of monetary power** from the interests collected. 
  It only takes a few keystrokes on a computer to create the money, so the reward-to-effort ratio is quite magnificent.
- The central banks also receive the benefit of **favorable policies and regulations** that, among other things, help 
  to keep the market clear of any **nasty competitors**.
- The governments gain happy subjects who are willing to **labor for the benefit of those in power** (i.e. the "common good")
- The government officials, as well as the preferred companies and social classes get nice **"free" money** (with a catch, of course)
- The rest of us get to **pay the bill**

What makes this process so viciously cunning (when operated correctly) is that:
- The people being plundered don't even realize they're being plundered
- "Political instability" is avoided and the governments keep on governing
- A facade of economic growth is preserved - After all, everyone is receiving more money
- Both the control-mechanisms and the flow of information is carefully controlled and not exposed to the public at large

The best thing is that, **because the governments always have our best interest at heart**, we get to enjoy continuous 
prosperity and **peace**. **Anarchy is avoided** and a great sense of **security** is preserved. Imagine what **chaos** it would 
be if everyone got to make their financial decisions **independent** of the nurturing oversight of their mother state? 
No, it is surely better that the power is kept within the hands of a **reliable and trustworthy minority**!  
(In case you haven't noticed it so far, I tend to use irony from time to time)

If you're interested on the matter. I highly recommend reading or listening to 
[Edward Griffin's book: "Creature from the Jekyll Island"](https://archive.org/details/TheCreatureFromJekyllIslandByG.EdwardGriffin). 
Notice that the audiobook is around 24 hours long, so if you find a "full" version around 8 hours long, that's not it.

### What can we do?
So, how does Arbiter help you in dealing with this possible reality? Arbiter provides you with a couple of very 
convenient tools with which you can establish your rates in gold, rather than in Euro or USD.

Why do I propose this is **beneficial to you and your company**?

First, by setting a price in gold, you **never need to adjust it again**, unless the quality of your services increase or 
the supply and demand -rate is affected. This has **2 major benefits**:
1. If you communicate this well with your customers, you get to enjoy **stable customer relations**, as they can clearly 
  see that you're not increasing prices, but that it's the purchasing power of their monetary unit of 
  choice that keeps getting weaker over time.
2. You get to **keep your income outside the direct market-manipulation** of those pulling the strings (such as the CPI)

Second, if you choose to increase your prices every year according to the CPI, 
you will face the following **negative consequences**:
1. Because there's reason to doubt the integrity of the CPI, 
  meaning that the actual rate of inflation may be **quite something else** than reported, you will have your **income decreased** 
  proportional to the discrepancy between CPI and reality. Do notice that this effect is **compounding**: You will **never 
  gain back** the rate you lost last year, as this year's CPI will only make matters worse.
2. Decreased income means that you either **have to work harder** to keep the same standard of living, or you have to 
  **decrease your standard of living**. You may have already noticed some **subtle hints** in the media, prompting you to 
  "save energy" and to "sacrifice for the common good". Just a though, but these things might be related.
3. While many businesses understand the necessity of the regular increase in prices, there may still be a subconscious 
  **loss in favor** when you do so. The effect for business-to-customer businesses is arguably worse, as "normal workers" 
  are more detached from the reality of the money market than those running a business.

If you, for some insane reason, choose n**ot to increase your prices at all**, you make your situation **way worse** concerning 
points 1 and 2, while lessening the effect of point 3.

By lessening you dependency on Euro and other fiat currencies, for example by adopting gold-based pricing, 
you help yourself (and possibly others) to **escape these negative consequences**.

### Why using gold is not ideal
Here are some points to consider before setting your prices in gold instead of in Euro or USD, 
as it may not be as "easy" as it sounds.

First, **many people perceive gold as a volatile asset and Euro/USD as a stable, reliable asset**. 
As a consequence, they may be very skeptical on accepting a price set in gold. Remember, from the 
"normal" person's point of view, the gold prices are climbing higher and higher by the year while their favored 
currency always remains the same. As a consequence, I would recommend looking into this matter yourself and then, 
if you're still convinced, to talk with your customers about it. If they don't buy the idea, consider not 
selling them on it. **You can still use these features** to check how you should adjust your "Euro-based" pricing, 
assuming you're free to make your own price-decisions (and I really hope you are).

Second, we're often not only dealing with independent customers but with **larger supply-chains**. Your customer 
may be tied in a contract where they're forced to follow the CPI. If you start basing your prices on gold, 
while they continue using the CPI, **someone's going to pay** for the price-fluctuations between the two currencies. 
This may make your customer reluctant to accept a contract set in gold. **However**, if you're in a position to do so, 
I do recommend **not entering into a CPI-based contract yourself**. While it may please your customer to know that 
they will never have to pay for the inflation themselves, you've only moved the burden from them to you if you do so. 
If you accept such a contract, **you're the one who has to worry** about losing your purchasing power because of 
the decision of some central bank bureaucrat.

Third, **gold markets can also be manipulated**. Central banks own a large portion of the world's gold supply. 
By purchasing and selling large quantities of gold at a time, they can cause major shifts in the gold markets. 
You don't necessarily want to tie yourself into that market control, if you're able to avoid it. Obviously 
you can't avoid market-manipulation by staying in a fiat currency either, as these are much easier to control by the 
central banks. Governments may also affect the price of gold by establishing various (unlawful) laws and policies. 
For example, a country may make the trading of gold illegal, or declare a tax on gold trade. These obviously affect the 
price of gold, and therefore your income as well.

### Recommendations
I personally would recommend weighing these costs and benefits to determine the use-cases where using a gold-based 
approach is beneficial. I personally chose gold for this algorithm for the reason that **1)** the quantity of the gold 
supply is very stable and varies little, and **2)** data concerning the price of gold is easily accessible via APIs. 
If better APIs were available, for example concerning the money supply itself, I would utilize those, also. If you happen to 
bump into a nice data source on the matter, please let me know.

In the future, I personally hope to see communities that utilize gold to a larger extent both in their contracts, and as a 
real monetary unit. Such communities would, in this area of finances, enjoy great liberty and relative independence from 
the worldly governments. That's obviously not the only area where these communities would have to exercise independence, 
but it's a start. We shall see whether this vision becomes reality at some point. Whatever the 
case, I'm sure it **won't** become a reality without conscious effort from people like you and me 
(thanks for reading so far, by the way).

### How to use the gold-related commands
Finally, here are the **gold-related commands** available to you in Arbiter:
- `goldvalue <period>` - Displays the recent average price of gold in Euros
- `valueof <euros> <period>` - Shows the value of the specified Euro-amount in gold
- `correct <original Euro price> <date of the original price> <period>` - Shows the **inflation-corrected** Euro value of a 
  previously agreed Euro-based price

The `<period>` argument used in these commands determines the number of days from which the gold price is collected, 
before it is reduced to its average. If the value is very small (such as 1), there may be relatively large fluctuations 
in the resulting gold price values, as the momentary supply and demand change. If the value is very large 
(such as 180), the price trend is delayed. The default value is 30, which should provide a quite reliable average.

Here, the most complicated command is the `correct` command. You can use this command to adjust your prices according 
to inflation (assuming that gold provides a reliable inflation-measurement). Simply specify a previously used price, as 
well as the (first) date when that price was used, and you will receive the inflation-adjusted price for today. 
The adjusted price will buy you the same amount of gold as you would have acquired with the price you specified 
on the specified date (using average gold prices). This command gives you an idea how much you should adjust 
your prices each year, for example, depending on your use-case.

For the gold-related commands to work, **you need to create a free account** in the 
[Metal Prices API](https://metalpriceapi.com) and provide the API-key when Arbiter requests for it. 
The free account will get you 30 requests each month, which should serve you well in this use-case. 
Arbiter caches the requested gold prices, which reduces the request usage.

## Feature-requests and other opportunities
If you have a **feature-request or a bug report**, please let me know. 
Also, if you're a **software developer** and would like to collaborate on some features, please contact me via email.

I've built this software to suit the needs of my own company. There is obviously a lot of room for additional 
features (such as an actual GUI). The challenge is that I don't really get compensated for my work on 
this software at the moment, and therefore I have to allocate my time sparingly. 
If you want to see more improvements, please consider **contributing**.

### How can you contribute?
There are many ways in which you can help me improve this software by collaborating with me.  
These ways include:
- Financial gifts
- Software development efforts (i.e. working together with me on this project)
- Helping me with sales and productization
- Increasing awareness of this software (and my software development library, Utopia) in the social media, etc.
- Prayer and intercession (in the name of Jesus; No white magic, thank you)

Right now this project doesn't produce me any financial gain. 
But, in the case it will at some point, I would see it reasonable to share that gain with the collaborators, also 
(according to their role, of course). For example, if you manage to turn this application into a product and to sell it, 
I believe you would be entitled to 50% of the sales value.

(I guess I can't prevent you from just keeping the 100%, but I can tell you that wouldn't be righteous and that 
you would, with such an action, open yourself to evil, and that you wouldn't actually be able to keep your gains. 
Just saying. Consider yourself notified.)

## License and user agreement
This software uses the following libraries:
- [Utopia library](https://github.com/Mikkomario/Utopia-Scala) - MIT license
- [Apache HttpClient](https://hc.apache.org/httpcomponents-client-5.2.x/) - Apache 2.0 license
- [MariaDB4j](https://github.com/MariaDB4j/MariaDB4j) - Apache 2.0 license

This software is available under the **MIT license**, with the **following additions**:

As far as I am concerned, **you're free to utilize this software** as part of your business practice, free of charge. 
You're also **free to copy and to modify** the software as you see fit. 
If you make a profit using this software in a righteous way, may that profit be blessed and serve you well.

Note that **this software comes without any warranty**. By downloading, using and/or distributing this 
software, you agree that I (Mikko Hilpinen) shall not be held liable for any damages this software might cause 
to you, your clients or any other party. You're advised to use discretion when using this software. 
**You shall be held solely responsible for 
all the consequences of using and/or distributing this software**. You've been notified that you have had access to this 
software's source code and have had good opportunity to verify whether it serves your use-case and whether it 
poses any risks to you, your organization, your clients or any other parties.

Attempts to **present this software as your own making** shall be considered **unlawful**. 
You shall have the ownership of any **modifications** you make to your **personal copy** of the software, 
but **not to the unmodified source code** you've downloaded. Contributions made into the shared project repository 
are considered **free gifts** towards the project, unless specifically agreed otherwise.
Also, attempts to pose myself (Mikko Hilpinen) 
as the owner and/or person responsible for the **modifications** you've made to your copy of the software shall 
be considered **unlawful**.

By acting against me on these matters, and/or by acting against me in any way that is considered unlawful, 
unrighteous or wicked by the common Christian law, you place yourself under the judgement of the heavenly court; 
Additionally, Ecclesiastical courts that operate under the lordship of Jesus the Christ shall be considered to 
adequately represent the heavenly court on these matters, if applied.