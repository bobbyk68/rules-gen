# Rules Text Parser & IR Demo

This repository demonstrates a text-based rule parser and Intermediate Representation (IR) generation system, designed for processing business rules (e.g., customs declarations). It includes a mechanism to translate natural language-like rule definitions into executable logic.

## Overview

The project provides:
- A rule parsing pipeline that takes rule definitions (CSV-like rows) and processes them.
- A Scalar Path Resolver to map text paths (e.g., `ConsignmentShipment.BorderTransportMeans.mode.code`) to internal object models.
- A demonstration of processing BR455 and BR675 rules.
- A helper script to generate Confluence-compatible documentation from CSV.

## Prerequisites

- Java 17
- Maven 3.x

## Build

To build the project, run:

```bash
mvn clean install
```

## Running the Demos

There are two main demo applications included in the project:

### 1. Rule IR Smoke Test
This demo runs a smoke test of the rule processing pipeline using a predefined set of rules (BR455). It parses the rules and outputs the processing steps.

To run it:

```bash
mvn exec:java -Dexec.mainClass="uk.gov.hmrc.rules.demo.RuleIrSmokeTest"
```

### 2. Resolver Demo
This demo shows how the `ReflectionScalarPathResolver` resolves paths against the `DemoFacts` data model.

To run it:

```bash
mvn exec:java -Dexec.mainClass="uk.gov.hmrc.rules.demo.ResolverDemoMain"
```

## Project Structure

- `src/main/java/uk/gov/hmrc/rules`: Core logic.
  - `demo`: Demo applications and data.
  - `dsl`/`dslr`: Domain Specific Language related code.
  - `ir`: Intermediate Representation of rules.
  - `parsing`: Text parsing logic.
  - `pipeline`: Rule processing pipeline.
  - `ruleset`: Rule set definitions.
- `csv_to_confluence_table.py`: A Python script to convert a `merged.csv` file into a Confluence Markdown table.

## Utilities

### CSV to Confluence Table

The `csv_to_confluence_table.py` script reads a `merged.csv` file and outputs a formatted table for Confluence.

Usage:
1. Ensure `merged.csv` is present in the root directory.
2. Run the script:
   ```bash
   python3 csv_to_confluence_table.py
   ```
