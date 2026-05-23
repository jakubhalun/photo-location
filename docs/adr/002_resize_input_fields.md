# ADR 002 — Resize Input Fields and Increase Font Size

## Status
Accepted

## Context
The two drag-and-drop input fields (JPEG and KML) occupied 40% of the window height, making them disproportionately large relative to the output panel which held the actual results. The default font size in these fields also made the placeholder text hard to read at a glance.

## Decision
- Reduced the top row height from 40% to 25% of the window (FXML `RowConstraints percentHeight`).
- Increased the output row from 50% to 65% to use the freed space.
- Added `-fx-font-size: 16px` to both input TextAreas.

The fields remain large enough for comfortable drag-and-drop use while giving the output panel more room.

## Consequences
The output panel is more readable and dominant, as it holds the data the user acts on. No logic changes required.
