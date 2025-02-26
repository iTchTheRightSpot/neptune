# e2e module

Module runs end-to-end tests for the system where it ensures that the Gateway,
Inventory, and Order modules work together seamlessly as a unified system.
These tests simulate real-world scenarios where all services communicate and
process data as part of the end-to-end flow.

## Running the Tests

In your terminal run the command (NOTE: if api gateway port is not 8080, update to accurate port)
```bash
javac main.java && java main.java 8080
```
