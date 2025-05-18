# Changelog

All notable changes to this project will be documented in this file.


The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/) and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).
## [0.4.0] - 2025-05-19
### Changed
- New transaction screen elements animate sequentially when opening.


## [0.3.2] - 2025-05-19
### Fixed
- Pie chart now renders correctly in the Categories screen.

## [0.3.1] - 2025-05-19
### Changed
- Moved "Add Category Group" button to the sheet handle area on Categories screen

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
