# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/) and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.62.0] - 2025-05-24
### Added
- feat: Set up basic Compose Multiplatform module

## [0.61.0] - 2025-05-26
### Added
- feat: Combine Accounts with Settings into single More screen

## [0.60.1] - 2025-05-24
### Added
- feat: Provide haptic and jiggle feedback on invalid transaction form

## [0.60.0] - 2025-05-24
### Added
- feat: Display assets and liabilities as grouped bar chart

## [0.59.0] - 2025-05-24
### Added
- feat: Allow tagging transactions with hobbies
- feat: Display top hobby spending in Insights

## [0.58.0] - 2025-05-24
### Added
- feat: Add reminder creation button in empty state
- feat: Show reminders screen with combined upcoming items

## [0.57.0] - 2025-05-24
### Added
- feat: Enable save button only when required fields are filled
- feat: Mark optional fields in new transaction screen
- feat: Prefill merchant from last entry when selecting category

## [0.56.0] - 2025-05-23
### Added
- feat: Load recurring transactions when navigating calendar months
## [0.55.0] - 2025-05-23
### Added
- feat: Include calendar reminders in upcoming notifications


## [0.55.0] - 2025-05-23
### Added
- feat: Mark upcoming bills on calendar

## [0.54.0] - 2025-05-23
### Added
- feat: Redesign notification center with card layout and sticky headers

## [0.53.0] - 2025-05-23
### Added
- feat: Implement recurring notification worker and permission checks

## [0.52.2] - 2025-05-22
### Changed
- style: Redesign settings screen with modern layout and icons

## [0.52.1] - 2025-05-22
### Fixed
- fix: Always open transaction details when an account is tapped

## [0.52.0] - 2025-05-22
### Added
- feat: Show contextual popup when tapping daily spend bar

## [0.51.0] - 2025-05-22
### Added
- feat: Show tooltip with daily details and reminders on calendar dates

## [0.50.1] - 2025-05-22
### Fixed
- fix: Refine tooltip anchor, styling, and calendar highlights

## [0.51.0] - 2025-05-26
### Changed
- feat: render net worth as wick chart
- fix: filter empty bars on daily and weekly budget chart

## [0.50.1] - 2025-05-26
### Fixed
- fix: show 5 period bars on insights charts

## [0.49.2] - 2025-05-25
### Fixed
- fix: keep input values on validation errors
- fix: auto select default category when switching type
- fix: prefill dropdowns when editing transactions

## [0.49.1] - 2025-05-26
### Changed
- refactor: Use layout-based rendering for insights bar charts

## [0.49.0] - 2025-05-25
### Added
- feat: Show placeholder daily spending chart when no data is available

## [0.48.0] - 2025-05-22

### Added
- feat: Load real data for daily spending chart
- feat: Show correct currency symbol on home screen
- fix: Format amounts using locale-specific grouping

## [0.46.0] - 2025-05-22
### Added
- feat: Merge Insights and Trends screen with improved charts.

## [0.45.1] - 2025-05-22
### Fixed
- fix: Persist monthly budgets when setting a budget.

## [0.45.0] - 2025-05-22
### Added
- feat: Show daily spending bar graph in home header.

## [0.44.1] - 2025-05-22
### Changed
- refactor: replace Double with BigDecimal for monetary values.

## [0.45.0] - 2025-05-22
### Added
- feat: Carousel accounts on Home screen with account icons.

## [0.44.1] - 2025-05-22
### Fixed
- fix: Load existing transaction data when tapping a transaction item.

## [0.45.0] - 2025-05-22
### Added
- feat: add account balance adjustment with TransactionType.ADJUSTMENT.
- feat: enforce typed confirmation when deleting accounts.

## [0.43.0] - 2025-05-22
### Added
- feat: Simplify Settings screen layout with section headers.

## [0.42.2] - 2025-05-22
### Changed
- refactor: Convert home screen to scrollable layout.

## [0.42.1] - 2025-05-22
### Changed
- style: Add gradient styling to insights charts.

## [0.42.0] - 2025-05-21
### Added
- feat: Show transactions when selecting a category.

## [0.41.1] - 2025-05-21
### Fixed
- fix: include default Transfer categories when seeding data.

## [0.41.0] - 2025-05-21
### Added
- feat: Inline validation errors for New Transaction dialog.

## [0.40.0] - 2025-05-21
### Added
- feat: Show monthly budget suggestion for categories with goals.
### Fixed
- fix: Edit buttons on Categories screen now open working rename sheets.

## [0.39.0] - 2025-05-21
### Added
- feat: Display forecasted trend with dashed line and gradient fill.
- feat: Add Y-axis and clickable points to spending trend chart.

## [0.38.0] - 2025-05-21
### Added
- feat: Show travel mode banner with spending summary.
- feat: Allow customizing travel tag name.

## [0.37.0] - 2025-05-20
### Added
- feat: Introduce travel mode with currency conversion.

## [0.36.1] - 2025-05-20
### Fixed
- fix: allow blank amount fields and validate on submit.

## [0.36.0] - 2025-05-20
### Added
- feat: Add spending trends and forecast screen.

## [0.35.0] - 2025-05-20
### Added
- feat: Attach receipt photos and OCR details for quick entry.

## [0.34.0] - 2025-05-20
### Added
- feat: Bank accounts are now the default account type.
- feat: Quick bank name options for PH banks when creating a bank account.
- feat: Detect finance app usage with permission toggle.
- feat: Request notification permission on Notifications screen and recurring transactions.

## [0.33.0] - 2025-05-20
### Added
- feat: Import only financial SMS and pre-fill transactions via Gemini.

## [0.33.1] - 2025-05-20
### Fixed
- fix: give priority to transaction list scrolling when sheet is expanded.

## [0.32.0] - 2025-05-20
### Added
- feat: Toggle reminders for recurring transactions.

## [0.31.1] - 2025-05-20
### Fixed
- fix: correct unallocated funds calculation.

## [0.31.0] - 2025-05-20
### Added
- feat: Experimental SMS import button in Settings.

## [0.30.2] - 2025-05-20
### Fixed
- fix: deduct account balance when saving a new transaction.

## [0.30.1] - 2025-05-20
### Fixed
- Goals created from Set Budget now use the category name.
## [0.29.0] - 2025-05-19
### Added
- Navigate to Recurring Transactions from Settings.


## [0.29.0] - 2025-05-19
### Added
- View recurring transaction details with edit and delete actions.
- View upcoming recurring transactions in a new screen.

## [0.28.1] - 2025-05-19
### Changed
- Clarified contribution guidelines on versioning and changelog entries.

## [0.28.1] - 2025-05-19
### Fixed
- Close, edit and delete buttons now work in read-only transaction view.
- Close button layout matches other dialogs.

## [0.29.0] - 2025-05-19
### Added
- Display allocated and unallocated summary above budgets pie chart in Categories screen.

## [0.29.0] - 2025-05-19
### Added
- New loan account types for assets and spending.
- Accounts can record whether a loan is from an institution or a person.
- Credit card accounts now include an optional credit limit.

## [0.28.0] - 2025-05-19
### Added
- Set Budget now opens as a dialog similar to New Transaction.
- Goals can now be linked to a specific category.

## [0.27.0] - 2025-05-19
### Added
- Recurring transactions can now be scheduled directly from New Transaction.
- Displays an alert directing users to add an account when none exist.
- Field validation with error messages for required fields.

## [0.26.0] - 2025-05-19
### Added
- Month navigation for Insights calendar with updated transaction filtering.
- Navigation dialogs for creating new categories and category groups.

### Changed
- New Category and Category Group screens now use a floating toolbar with a Budget/Goal toggle.
- Budget/Goal toggle moved from the New Category form to the Set Budget sheet.
- Set Budget sheet redesigned with a floating toolbar.

## [0.26.0] - 2025-05-19
### Added
- Transactions now open in the New Transaction dialog with read-only fields and options to edit or delete.

## [0.25.1] - 2025-05-19
### Fixed
- Dialogs now move above the soft keyboard when typing.

## [0.25.0] - 2025-05-19
### Added
- Recurring transactions are now persisted using Room.

## [0.24.0] - 2025-05-19
### Added
- View wallet transactions in a bottom sheet when selecting an account.
- Edit or delete an account from the bottom sheet header.

## [0.23.0] - 2025-05-19
### Added
- Upcoming due reminders for recurring transactions in Notification Center.

## [0.23.0] - 2025-05-19
### Added
- Default "Transfers" category group with Adjustment, Goal Contribution and Loan Payment categories.
- New transactions default to the Adjustment category when Transfer is selected.
- Categories with goals show the due date and months remaining when no budget is set.

### Changed
- New Category Group and Category screens mirror the card layout of New Transaction.
- Goal due date row now uses a calendar icon and is fully clickable.

## [0.21.0] - 2025-05-19
### Added
- Progress bar for category budgets.
- Creating a goal now also creates a Goal Jar account.
- Toggle between Budget and Goal when creating a new category.

## [0.20.0] - 2025-05-19
### Added
- Scrollable Insights screen with individual date range dropdowns for each chart.
- Fixed calendar alignment issues.

## [0.19.9] - 2025-05-19
### Changed
- Replaced filter tabs with a docked tab row in the Notifications screen.
- Added sample actionable notifications.

## [0.18.0] - 2025-05-19
### Changed
- Redesigned New Account screen to mirror the New Transaction dialog.
- Fixed currency selector and moved account type selection to a dropdown on the floating toolbar.
- Loan accounts now toggle between **Paid Amount** and **Remaining Balance** using a switch.

## [0.17.0] - 2025-05-19
### Added
- Zero-based budgeting summary with calculation of unassigned funds on the home screen.


## [0.16.0] - 2025-05-19
### Added
- Ability to save financial goals for future spending or savings.

## [0.15.0] - 2025-05-19
### Added
- Toggle on Insights charts for selecting time period (daily, weekly, monthly, 3M, 6M, yearly).

## [0.14.1] - 2025-05-19
### Changed
- Category groups and categories are now movable by long pressing anywhere.

## [0.14.0] - 2025-05-19
### Added
- Calendar view showing bills and transactions in Insights screen.

## [0.13.1] - 2025-05-19
### Changed
- Currency symbols now reflect each transaction or account's currency.

## [0.13.0] - 2025-05-19
### Added
- Extended default outflow categories with additional groups and options.

## [0.12.0] - 2025-05-19
### Added
- Transaction details screen with edit and save functionality.

## [0.11.0] - 2025-05-19
### Added
- Notification Center now shows sections for **All notifications**, **Upcoming Bills Reminder**, and **Transaction Suggestions**.

## [0.10.0] - 2025-05-19
### Added
- Loan account type with contract value and monthly payment fields.
- Automatic liability category creation for loans.

## [0.9.0] - 2025-05-18
### Added
- Recurring transaction options can now be selected directly from the New Transaction screen.

## [0.8.0] - 2025-05-18
### Added
- Graph bars now have rounded edges and display a popup with budget or net worth details when tapped.
### Fixed
- Insights screen no longer crashes when data is missing.

## [0.7.0] - 2025-05-18
### Added
- Basic screen for setting recurring transactions with options for Monthly, Weekly, Daily and after cutoff date.
- Access recurring transaction setup from the New Transaction screen.

## [0.6.0] - 2025-05-18
### Added
- Edit mode toggle on Categories screen with edit and delete buttons per group and category.
- Budgets can now be edited from the Categories screen.
- Budgets now store their currency alongside allocation data.
- Currency is now stored for each account in the database.

## [0.5.1] - 2025-05-18
### Fixed
- Accounts total now calculates correctly.
- Total amount text aligns with the add account button.
- Inflow categories were filtered out and could not be selected on the New Transaction screen.
- Vertically center the cursor in the new transaction screen when the amount is zero.

### Changed
- Added a squiggly divider for a more playful Accounts screen.

## [0.5.0] - 2025-0518
### Added
- Prompt to add default outflow categories when none exist.
### Changed
- Inflow categories no longer appear in transaction or category lists and are ignored in budgets.

## [0.4.0] - 2025-05-18
### Changed
- New transaction screen elements animate sequentially when opening.

## [0.3.2] - 2025-05-18
### Fixed
- Pie chart now renders correctly in the Categories screen.

## [0.3.1] - 2025-05-18
### Changed
- Moved "Add Category Group" button to the sheet handle area on Categories screen

## [0.3.1] - 2025-0518
### Changed
- Moved "Add Wallet" button to the top of the Accounts screen so bottom controls no longer overlap.

## [0.3.0] - 2025-05-18
### Added
- Seeded an **Inflow** category group with default categories such as Salary and Allowance.
- New transactions default to the **Salary** category when the transaction type is Inflow.

## [0.2.5] - 2025-05-18
### Changed
- New category sheet now displays the selected category group name instead of its ID.

## [0.2.4] - 2025-05-18
### Added
- Display total account value in Accounts screen

## [0.2.3] - 2025-05-18
### Fixed
- Outflow transactions on the home screen are now shown with a red negative amount

## [0.2.2] - 2025-05-18
### Added
- Unit tests for ViewModels

## [0.2.1] - 2025-05-18
### Fixed
- Default transaction type selection is now Outflow.
- New transaction dialog now fills the entire screen width.

## [0.2.0] - 2025-05-18
### Changed
- Category groups and categories now use swipe gestures: swipe right to edit and
  swipe left to delete.

## [0.1.0] - 2025-05-18
### Added
- Skeleton loaders for screens when UI state is `Initial`
- Notification center with swipe to archive, read status and quick create transaction button
- AGENTS instructions for AI contributions
- Pie chart summary and bottom sheet layout for Categories screen

### Changed
- Amount field in new transaction screen now displays the cursor even when zero
  and treats the initial zero as a placeholder.
- Category groups and categories can now be reordered by long pressing anywhere,
  with options to edit or delete after long press while the rest of the screen
  is dimmed.

### Fixed
- Outflow transactions on the home screen are now shown with a red negative amount

## [0.0.1] - 2025-05-18
### Added
- Initial Changelog file
