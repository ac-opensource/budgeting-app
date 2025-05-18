# Changelog

All notable changes to this project will be documented in this file.


The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/) and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.7.0] - 2025-05-21
### Added
- Basic screen for setting recurring transactions with options for Monthly, Weekly, Daily and after cutoff date.
- Access recurring transaction setup from the New Transaction screen.

## [0.6.0] - 2025-05-21
### Added
- Edit mode toggle on Categories screen with edit and delete buttons per group and category.
- Budgets can now be edited from the Categories screen.
- Budgets now store their currency alongside allocation data.
- Currency is now stored for each account in the database.

## [0.5.1] - 2025-05-21
### Fixed
- Accounts total now calculates correctly.
- Total amount text aligns with the add account button.
- Inflow categories were filtered out and could not be selected on the New Transaction screen.
- Vertically center the cursor in the new transaction screen when the amount is zero.

### Changed
- Added a squiggly divider for a more playful Accounts screen.

## [0.5.0] - 2025-05-20
### Added
- Prompt to add default outflow categories when none exist.
### Changed
- Inflow categories no longer appear in transaction or category lists and are ignored in budgets.

## [0.4.0] - 2025-05-19
### Changed
- New transaction screen elements animate sequentially when opening.

## [0.3.2] - 2025-05-19
### Fixed
- Pie chart now renders correctly in the Categories screen.

## [0.3.1] - 2025-05-19
### Changed
- Moved "Add Category Group" button to the sheet handle area on Categories screen

## [0.3.1] - 2025-05-20
### Changed
- Moved "Add Wallet" button to the top of the Accounts screen so bottom controls no longer overlap.

## [0.3.0] - 2025-05-19
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

## [0.2.1] - 2025-05-19
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
