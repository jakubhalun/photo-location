# ADR 003 — Replace Plain Text Output with Rich Interactive Panel

## Status
Accepted

## Context
The output area was a read-only `TextArea` showing plain text. Two usability problems were identified:

1. **Links were not clickable.** OpenStreetMap and Google Maps URLs appeared as plain text and had to be manually copied into a browser.
2. **Copying the Commons template was tedious.** Users had to carefully select just the `{{Location|...|...}}` fragment from a wall of text.

## Decision

Replace the `TextArea` with a `ScrollPane > VBox` populated at runtime with native JavaFX nodes rendered by a new `LocationResultNodeRenderer` class. Each result section contains:
- Bold `Label` for section headers
- `Label` for coordinate/time information
- `HBox` with the Commons template text and a **📋 Copy** button
- `Hyperlink` nodes for OpenStreetMap and Google Maps that open in the system default browser via `java.awt.Desktop.getDesktop().browse()`

### Why not JavaFX WebView?

`WebView` (the `javafx.web` module) would allow rendering HTML with styled links and JavaScript-driven copy buttons. It was rejected because:
- It requires adding the `javafx.web` module as a dependency, which is large and has OS-specific native binaries.
- The shadow JAR would grow significantly.
- The existing dependency set (`javafx.controls`, `javafx.fxml`) is sufficient for native nodes.
- WebView introduces a full browser engine (WebKit) for a problem that native JavaFX nodes solve cleanly.

### Why add a "Copy all as text" button?

With native JavaFX `Label` nodes, **free text selection across the panel is not possible**. Unlike `TextArea`, Labels are not selectable by design — they are display-only nodes with no text cursor or selection model.

Alternatives were considered:
- **`TextField(editable=false)`** per line: each field is individually selectable but only one at a time; multi-line content cannot be selected across fields. Visual appearance also differs (box borders around every line).
- **`TextFlow` with `Text` nodes**: supports inline text selection within a single `TextFlow`, but cannot mix selectable text with interactive `Button` and `Hyperlink` nodes in the same flow.

A single **📄 Copy all as text** button at the top of the panel was added as the pragmatic solution. It reconstructs the full plain-text representation (identical to the old `TextArea` output) and writes it to the clipboard, restoring the ability to freely copy any part of the result when needed.

## Consequences
- Links are now one-click to open.
- The Commons template can be copied with a single button per point.
- The full output remains accessible via the "Copy all as text" button.
- `LocationResultTextFormatter` was deleted as it became unused.
- No new runtime dependencies were introduced.
