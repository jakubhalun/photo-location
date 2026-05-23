# ADR 001 — Add Label to Time Adjustment Selector

## Status
Accepted

## Context
The bottom ComboBox that adjusts the photo timestamp had no visible label in the UI. Users had no indication of what the values (`+1:00`, `-2:00`, etc.) meant or how they related to timezones. The FXML contained only an XML comment (`<!-- Difference to UTC selection -->`), invisible at runtime.

## Decision
Added a `Label` next to the ComboBox inside an `HBox` container with the text:
> *"Camera time correction (add to photo time to get UTC, e.g. -2:00 for UTC+2 camera):"*

The label makes the semantics explicit: the selected value is **added** to the photo timestamp, so it is the inverse of the camera's UTC offset (e.g. `-2:00` for a camera set to UTC+2).

## Consequences
Users now understand the selector without reading the README. No logic changes were required.
