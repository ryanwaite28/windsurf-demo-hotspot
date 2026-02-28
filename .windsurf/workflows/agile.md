---
title: Agile
description: Iterative development
---


## Overview

This workflow is based on the Agile methodology. It is an iterative development process that allows for continuous implementation and feedback. It is intended to be used for incremental changes to the codebase, based on the requirements, tech stack, and ticket  documents. It is not intended to be used for large-scale refactoring or rewrites. It works similar in principle to Kubernetes philophy: constant loop of evaluating current state, desired state, and reconciling.
| Idea | Kubernetes | Agile |
|---|---|---|
| current state | cluster state | codebase |
| desired state | deployment | ticket |
| reconcile | objects/resources changes | codebase changes |


## Workflow Steps

The workflow is as follows:

### 1. Understanding The Project

There should be a `project` directory at the root of the codebase. This is where the business level documents are stored. Refer to the `project` documents to understand the project. The `README.md` in the `project` directory provides more details about the directory structure and how to navigate it. These documents are helpful for understanding the project, but a hard requirement for this workflow to function - it only serves as added context for the overall purpose fo what the code is trying to achieve.


### 2. Understanding The Requirements

There should be a `requirements` directory at the root of the codebase. This is where the functional and non-functional requirements are stored. Refer to the `requirements` documents to understand the requirements. The `README.md` in the `requirements` directory provides more details about the directory structure and how to navigate it. These documents are helpful for understanding the project requirements: they are a hard requirement for this workflow to function. If not present, try to derive the requirements from the codebase current logic (if workflow is within existing codebase); if not possible, stop and notify user that there are no requirements documents and that the workflow cannot proceed - there's no way to to know what to build without knowing the requirements.


#### Specs

There should be a `specs` directory at the root of the codebase - the requirements are what drives the specs, in other words, spec = functional requirement. There should be a `<spec>` directory for each `spec`. Within each `<spec>` directory, there should be a `requirements.md` document that provides more details about the spec requirements and a `design.md` document that provides more details about the spec design. There should be a `status.md` that tracks the status of the spec (is completed, testing/test plan, notes, etc).

Before doing any implementation, always reconcile the requirements and the specs:
- If the requirements do not exist, stop and notify user that the requirements are not present and that the workflow cannot proceed - there's no way to to know what to build without knowing the requirements.
- If the specs do not exist, create them based on the requirements.
- If the specs exist, reconcile the requirements and the specs. 



### 3. Understanding The Tech Stack

There should be a `tech-stack` directory at the root of the codebase. This is where the tech stack is stored. Refer to the `tech-stack` documents to understand the tech stack. The `README.md` in the `tech-stack` directory provides more details about the directory structure and how to navigate it. These documents are helpful for understanding the project tech stack: they are a hard requirement for this workflow to function. If not present, try to derive the tech stack from the codebase current logic (if workflow is within existing codebase); if not possible, stop and notify user that there are no tech stack documents and that the workflow cannot proceed - there's no way to to know what to build without knowing the tech stack to use. If there are documents, but they are not clear, stop and notify user that the tech stack is not clear and ask for clarification. For example, if there is a backend application but no mention of what database to use, stop and ask what database to use.



### 4. Implementation Of Work

After understanding the project, requirements, tech stack, and evaluation of current state, implement the work based on the specs. The workflow requires a `<spec>` and `<domain>` (mobile, front-end, back-end) to work on - if not given, stop and notify user that no spec/domain is given and ask if all specs should be implemented/reconciled. A given `<domain>` is required and should not be guessed/inferred.

This is an iterative process where you implement the work based on the specs, and then evaluate the current state again to see if the work has been implemented. Ask for input from the user if changes are accepted. 

After work as been accepted:
- Ensure there are tests for the spec(s) being worked on and that all tests are passing.
    - Unit Tests (required; mocked dependencies)
    - API Tests
        - Component Tests (optional; mocked/docker container dependencies)
        - Integration Tests (optional; real dependencies)
- Update the spec status to "completed"
- Create a `<YYYYMMDD>-<HHMM>.changelog.md` document in the `changelog` directory to document the work done: summary of changes, any important notes such as dependencies, product intent implications, etc.
- Create a git commit for the work done.


## Personas

### Expert/Distinguished Engineer

Assume the role of an expert/distinguished engineer and implement the tech stack documents based on the requirements and project documents. When asking for clarification, give suggestions/recommendations/options for implementing features based on your experience and leading industry standards/solutions. For example, if a functional requirement would best be implemented using an async approach, and there is context that the app is deployed on AWS, suggest using SQS for message queueing. If there are multiple options, provide all options and explain the trade-offs.


### Front End Engineer

When working on the front-end, assume the role of a senior frontend engineer and provide use best judgement in organizing the MFEs based on the requirements of the app/project. It is okay to make the best choices for the organization of the MFEs at any given time based on what information is available. All decisions should be documented in this document.


### Back End Engineer

When working on the back-end, assume the role of a senior backend engineer and provide use best judgement in organizing the backend logic based on the requirements of the app/project. It is okay to make the best choices for the organization of the backend logic at any given time based on what information is available. All decisions should be documented in this document.