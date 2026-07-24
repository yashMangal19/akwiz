# How this was built

Short version: I planned it before writing it, built it bottom-up in phases, and tested each phase
before moving to the next. This is the story of the decisions and how it came together.

If you want the one-line summary of the whole thing: **pick the simple option, and be able to say
what would make you pick the harder one.**

---

## The approach

- **Plan first.** Before any code, I wrote down what the app had to do, turned the brief into a
  checklist, and settled the ambiguous bits (does a skip break the streak? can you go back? — no and
  no).
- **Build bottom-up.** Data → logic → screens. When a test breaks, there's only one place the bug
  can be. Building the screens first feels faster and then costs more.
- **Test as you go.** Each phase ended with tests passing, not a promise to test later.

---

## The decisions

Each of these was a fork with a simpler and a heavier option. I picked the simpler one and wrote
down what would change my mind.

**Kept the layers apart.** The screens, the logic, and the data don't reach into each other. The
logic doesn't import anything Android, so the whole quiz can be tested on a laptop with no phone.

**One state object per screen.** Instead of a pile of separate flags (isLoading, isError, …), each
screen is one of a fixed set of shapes — Loading, Error, a running quiz, finished. You can't get
into a nonsense in-between state, because there's no way to write one.

**Network, then disk, then bundled.** It fetches the questions online, falls back to the last copy
it saved, and falls back again to a copy shipped inside the app. So it works on a plane.

**Two small storage files, not one.** The saved questions and your progress are kept separate,
because if the questions file ever gets corrupted I don't want it to take your best streak down with
it.

**Skipped the big libraries.** No Hilt, no Room, no navigation library. For ten questions and two
screens they'd be more to explain than they'd save. Each one has a note on the exact point where I'd
add it (more screens, more data, a second feature).

**Borrowed a colour palette instead of inventing one.** Used Rosé Pine, an open palette with an
actual point of view — and it happens to have no green, so "correct" is teal and "wrong" is rose,
which stays readable if you're colour-blind. I checked every colour against contrast rules and
darkened the few that failed.

**Screenshot tests for the screens.** Every screen is rendered in a test and compared to a saved
image, so a styling change that shifts something shows up as a failing test instead of slipping
through.

**The review is a peek, not a page.** The answer review opens in a bottom sheet you pull up and
dismiss, not a whole new screen. You glance at it and go back — that's a sheet, not a destination.

**Made the auto-advance accessible.** The 2-second move-to-next is fine until you turn on a screen
reader, which can't read the result that fast. So with a reader on, it shows a "Next" button and
lets you go at your own pace.

---

## The phases

Each was one focused, testable step.

| # | Phase | What it added |
|---|---|---|
| 0 | Design & assets | Colours, fonts, the app icon, the celebration animations |
| 1 | Build setup | The libraries, all at once, before any feature depended on them |
| 2 | Models | The plain data types everything else speaks in |
| 3 | Data — network | Fetching and validating the questions, with the bundled fallback |
| 4 | Data — storage | Caching, saving progress, the best streak |
| 5 | The state machine | The whole quiz as logic, fully tested, with no screens yet |
| 6 | Components | The reusable pieces — option cards, the streak badge, the score ring |
| 7 | The question screen | Wiring the logic to the screens — the app plays for the first time |
| 8 | Results & review | The score, the per-answer review, the resume prompt |
| 9 | Polish & access | Transitions, swipe-to-advance, the screen-reader work |
| 10 | Verify & ship | Testing of edge cases, this write-up, the README |

By the end of phase 5 the entire quiz worked in tests before a single screen existed. Phases 6–9
just drew the screens for logic that was already proven right.

---

## Checking it works

The automated tests cover the logic and the look. Some things only a real phone can prove —
rotating mid-answer, killing the app and reopening, a proper screen-reader pass. Those are in the
[manual checklist](MANUAL-QA.md). I ran the main flow on a physical phone (the screenshots in the
README are from it), including resume-after-close and the review sheet.

---

## On using AI

I used an AI assistant (Claude) to help with this. Being straight about it: there was a lot of code
and a lot of tests, and a well-thought-out plan run through AI is what made that amount of work
finishable in the time.

What that looked like in practice: I made the decisions and set the direction — which architecture,
which trade-offs, what to build and what to leave out — and the AI did a lot of the execution,
writing code and tests against that direction. Every choice was mine to approve, and I read every
line that went in.

I think that's the honest and useful way to work with these tools — not to hand over the thinking,
but to move faster on the doing once the thinking is done.
