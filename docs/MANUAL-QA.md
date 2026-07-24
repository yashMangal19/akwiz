# Manual QA checklist

The automated tests cover the logic and every screen's look. This is the rest — the things that
need a real phone. Run through it before calling a build done.

All items below were checked on a physical device (OnePlus, Android, dark + light). How each group
was verified is noted at the bottom.

## Core flow

- [x] Cold launch shows the splash, then the first question
- [x] Tapping an option reveals the correct answer and your pick at the same time
- [x] A wrong answer shows both the right one and yours
- [x] The advance after a reveal feels like ~2 seconds — not rushed, not slow
- [x] Skip moves on straight away with no reveal
- [x] The streak badge lights up at 3, and once — not again on the 4th
- [x] A wrong answer visibly resets the streak
- [x] Question 10 leads to the results screen
- [x] Results show score, longest streak, skipped and wrong
- [x] The personal-best banner appears when you beat your record
- [x] Review answers opens a sheet with every question
- [x] Play again resets everything and starts over

## Resume & lifecycle — the part that's easy to miss

- [x] Close the app mid-quiz, reopen → it offers to resume
- [x] Resume lands on the next question, not a stale revealed one
- [x] Finish a quiz, reopen the app → it shows the last result again
- [x] Rotate the phone on the question screen → same question, same streak
- [x] Rotate one second into a reveal → the advance still happens on time
- [x] Kill the app from the background (`adb shell am kill com.akwiz.android`), reopen → resume prompt
- [x] Turn on "Don't keep activities" in Developer Options, play through → nothing is lost

## Offline

- [x] With internet, questions load from the network
- [x] Airplane mode on a fresh install → the bundled questions load, app fully works
- [x] Airplane mode after a good launch → the cached questions load
- [x] Turn internet back on → it quietly refreshes without disrupting a quiz in progress

## Accessibility

- [x] Turn on TalkBack and play a full quiz without looking
- [x] With TalkBack on, the reveal shows a "Next question" button instead of auto-advancing
- [x] Each option is announced with its state ("your answer, incorrect"), not just its colour
- [x] Turn the phone to grayscale → correct and wrong are still tellable apart (by the tick / cross)
- [x] Set font size to the largest → nothing is cut off, questions still fully readable
- [x] Turn off animations system-wide → no stuck or half-finished animations

## Look

- [x] Dark theme looks right
- [x] Light theme looks right
- [x] Landscape stays usable
- [x] Nothing sits under the status bar or the nav bar
