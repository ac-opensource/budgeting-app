# Changelog

All notable changes to this project will be documented in this file.


The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/) and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.26.0] - 2025-05-19
### Added
- Recurring transactions can now be scheduled directly from New Transaction.
- Displays an alert directing users to add an account when none exist.
- Field validation with error messages for required fields.

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
