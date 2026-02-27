# Tech Stack: HotSpot

This directory contains the tech stack for the HotSpot project. Each file in this directory is an addendum to the tech stack, with the exception of the README.md and where the first file is the initial version and last file is the latest version. Each file is named in the format YYYYMMDD-HHMM.tech-stack.md - files should be sorted in ascending order by date and are treated as immutable. This is to keep track of the tech stack over time and to allow for easy reference to previous tech stack states.

The `tech-stack` documents are the technical implementation details for the project. They are not intended to be used for business level decisions, but rather for technical implementation/tools details. 

## Terminology

- Asset: A resource for implementing some functionality (e.g. a database, an s3 bucket, a redis cache, ecs cluster/service/container, etc)
- Component: A collection of assets that implement some high level capability (users service, auth service, social service, etc)
- Environment: A deployment of a component (e.g. dev, qa, prod)
