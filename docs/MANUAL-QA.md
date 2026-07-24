# Manual QA checklist

The automated tests cover the logic and every screen's look. This is the rest — the things that
need a real phone. Run through it before calling a build done.

`✅` = already checked on a physical device (OnePlus, Android, dark mode). The rest are for a full
pass across devices and settings.

## Core flow

- [x] Cold launch shows the splash, then the first question
- [x] Tapping an option reveals the correct answer and your pick at the same time
- [x] A wrong answer shows both the right one and yours ✅
- [ ] The advance after a reveal feels like ~2 seconds — not rushed, not slow
- [ ] Skip moves on straight away with no reveal
- [ ] The streak badge lights up at 3, and once — not again on the 4th
- [ ] A wrong answer visibly resets the streak
- [x] Question 10 leads to the results screen ✅
- [x] Results show score, longest streak, skipped and wrong ✅
- [x] The personal-best banner appears when you beat your record ✅
- [x] Review answers opens a sheet with every question ✅
- [ ] Play again resets everything and starts over

## Resume & lifecycle — the part that's easy to miss

- [x] Close the app mid-quiz, reopen → it offers to resume ✅
- [ ] Resume lands on the next question, not a stale revealed one
- [x] Finish a quiz, reopen the app → it shows the last result again ✅
- [ ] Rotate the phone on the question screen → same question, same streak
- [ ] Rotate one second into a reveal → the advance still happens on time
- [ ] Kill the app from the background (`adb shell am kill com.akwiz.android`), reopen → resume prompt
- [ ] Turn on "Don't keep activities" in Developer Options, play through → nothing is lost

## Offline

- [x] With internet, questions load from the network ✅
- [ ] Airplane mode on a fresh install → the bundled questions load, app fully works
- [ ] Airplane mode after a good launch → the cached questions load
- [ ] Turn internet back on → it quietly refreshes without disrupting a quiz in progress

## Accessibility

- [ ] Turn on TalkBack and play a full quiz without looking
- [ ] With TalkBack on, the reveal shows a "Next question" button instead of auto-advancing
- [ ] Each option is announced with its state ("your answer, incorrect"), not just its colour
- [ ] Turn the phone to grayscale → correct and wrong are still tellable apart (by the tick / cross)
- [ ] Set font size to the largest → nothing is cut off, questions still fully readable
- [ ] Turn off animations system-wide → no stuck or half-finished animations

## Look

- [x] Dark theme looks right ✅
- [ ] Light theme looks right
- [ ] Landscape stays usable
- [ ] Nothing sits under the status bar or the nav bar

## Notes from the device run

Ran the main flow on a physical phone in dark mode: fresh start, a wrong answer, through to results
(8/10, a 5 streak, personal best), and opened the review sheet — all working. The resume prompt
showed up from a previous session, so persistence is confirmed across app restarts on real hardware.
