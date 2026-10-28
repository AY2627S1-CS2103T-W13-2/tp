---
layout: page
title: Logging guide
---

* The project uses the `java.util.logging` package for logging.
* `LogsCenter` manages logging levels and destinations.
* A class obtains a `Logger` with `LogsCenter.getLogger(Class)`, which logs messages at the configured level.
* Log messages are written to the console and a `.log` file.
* The output logging level can be controlled by changing the `LOG_LEVEL` constant in the `LogsCenter` class.
* `LogicManager` logs the parsed command implementation type and a fixed outcome: `succeeded`, `rejected`,
  or `save failed`. Parsing failures use the fixed message `Command parsing rejected`; even an unknown command
  word can contain participant data and must not be logged. Success is logged only after saving completes.
* Do not log raw commands, arguments, command objects, result feedback, participant objects, or exception messages
  from command processing, including at debug levels. The UI still displays full feedback to the user.
  Save-failure diagnostics retain the file path and exception type without serializing the exception or its cause.
* `CommandLoggingTest` exercises the JavaFX command box, parser, logic and model reload with all logging levels
  enabled. It checks successful add/edit/remark/view/delete, malformed input, unknown commands and execution
  failures. `LogicManagerTest` checks save-failure logging separately.
* This policy does not scrub historical logs. Old logs can retain personal data; filesystem paths and other
  diagnostics also remain. Review and redact logs before sharing them.
* **When choosing a level for a log message**, follow the conventions given in [_[se-edu/guides] Java: Logging conventions_](https://se-education.org/guides/conventions/java/logging.html).
