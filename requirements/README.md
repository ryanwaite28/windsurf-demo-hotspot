# Requirements: HotSpot

This directory contains the requirements for the HotSpot project. Each file in this directory is an addendum to the requirements, with the exception of the README.md and where the first file is the initial version and last file is the latest version. Each file is named in the format YYYYMMDD-HHMM.requirements.md - files should be sorted in ascending order by date and are treated as immutable. This is to keep track of the requirements over time and to allow for easy reference to previous requirements states.

### Spec Driven Development

Each "Functional" requirement is a spec for functionality that must be implemented in the app (login, create post, etc). "Non-Functional" requirements are for aspects of the app that must be met (performance, security, etc) - they are not specs. Specs can recursively contain other specs, allowing for a hierarchical structure of requirements and pin-pointing what features/bugs are being worked on.
