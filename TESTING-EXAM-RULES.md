# Testing the exam rules (open once, Safe Exam Browser, answer-times report)

This file is meant to be read by whoever tests these features, **including an assistant that has no history of
how they were built**. It says what exists, where it lives, how to set up the Safe Exam Browser (SEB) on Windows and
what to check.

## What was built

All of it is per quiz and off by default (**Management › Quizzes › New Quiz / edit**, switches under *Timer*):

| Option | What it does | Where |
|---|---|---|
| **Open only once** (`singleSession`) | The student can open the quiz one time. Closing or reloading the page ends their chance. Refused attempts are counted. A teacher can press **Let open again** (answers of the quiz dialog). | `AnswerService.openSession` / `QuizAnswer.openSession`, `AvailableQuizzesView.vue` |
| **Only in the Safe Exam Browser** (`safeBrowserOnly` + `sebConfigKey`) | Opening, answering and concluding the quiz is refused unless the request carries SEB's proof. | `quiz/SafeBrowserCheck.java`, `quiz/SafeBrowserAccess.java` (interceptor), `frontend/src/services/SafeExamBrowser.ts` |
| **Answer times report** | Button *Answer times report* on the quizzes page: students whose times look unlike a person reading, over all the quizzes of the course. | `fraud-service/fraud_times/speed.py`, `SpeedReportDialog.vue` |

Also fixed: the quiz timer did not update on screen nor conclude the quiz at zero (a Vue 3 reactivity bug in
`StatementQuiz.ts`).

## How the Safe Exam Browser check works

SEB proves it is itself by sending, with its requests, `SHA-256( <address> + <Config Key> )` in hexadecimal. The
server knows the Config Key of the quiz, computes the same hash and compares. The server **cannot see which browser
it is**: it only checks the proof, so the Config Key is a secret (teachers see it; students never do).

Two forms of the proof are accepted:

1. The header SEB adds to its own requests: `X-SafeExamBrowser-ConfigKeyHash`, made from the address of the request
   (or of the page that made it, from `Referer`).
2. The pair `X-SEB-Page-Url` + `X-SEB-Config-Key-Hash`, which the Tutor's page sends from SEB's JavaScript API
   (`SafeExamBrowser.security.configKey`, available in SEB for Windows from 3.4). This is the one to rely on because
   the Tutor makes its calls from script and it is not certain that SEB adds its header to those.

**This has never been tried with a real SEB.** Doing that is the purpose of this test.

## Set up SEB on Windows

1. Download and install the Safe Exam Browser from <https://safeexambrowser.org/download_en.html> (use a current
   Windows version; it includes the *SEB Configuration Tool*).
2. Open the **SEB Configuration Tool**. Set:
   * **General › Start URL**: the address of the Tutor frontend, exactly as students will use it
     (for example `http://localhost:8081` for a test on the same machine, or `https://<your server>`).
   * **General › Set administrator password** and allow quitting (a quit password), or you will not be able to leave
     the browser easily.
   * **Exam** tab: tick **Use Browser & Config Keys (send in HTTP header)**. Then look on that tab for the
     **Config Key** (64 hexadecimal characters) next to the Browser Exam Key; the official manual for SEB 3.x is
     incomplete about where it is shown, so if you cannot find it, hover the fields for their tooltips.
   * Save the configuration as a `.seb` file. **The Config Key changes whenever any setting changes**, so after
     changing anything copy it again and update the quiz.
3. In the Tutor, create or edit a quiz, switch on **Only in the Safe Exam Browser**, paste the Config Key and save
   (a quiz that already has answers cannot be edited: use a new one).
4. Start SEB by double-clicking the `.seb` file and log in to the Tutor inside it.

## Checklist

Use a timed quiz that is available now, and a student account (a demo student works on a development server).

| # | Do | Expected |
|---|---|---|
| 1 | In an ordinary browser (Chrome, Firefox), log in as the student, open the quiz | Error: *This quiz can only be done in the Safe Exam Browser* |
| 2 | In SEB, log in as the same student, open the quiz | The quiz opens; the clock at the top counts down every second |
| 3 | In SEB, answer and conclude | Works; the answers appear to the teacher |
| 4 | Create another quiz with **Open only once** (and SEB), open it in SEB, then close the page/reload and try to open it again | Error: *This quiz can only be opened once…*; the teacher sees `1 refused` and **Let open again** |
| 5 | Teacher presses **Let open again**; the student opens it again | Opens |
| 6 | A quiz without the SEB option, in an ordinary browser | Works as before |
| 7 | Teacher opens **Answer times report** | A list (empty if nobody stands out) with the text "never proof" |

## If step 2 fails (SEB is refused)

The backend logs a line each time it refuses a proof, **without the key or the hashes**:

```
Safe Exam Browser proof refused for quiz 7: request https://…/answers/7/start, header X-SafeExamBrowser-ConfigKeyHash present|missing, page X-SEB-Config-Key-Hash present|missing, X-SEB-Page-Url …, referer …
```

Read it to see which case you are in:

* **both missing**: SEB is not sending the proof. Check that *Use Browser & Config Keys (send in HTTP header)* is ticked
  in the configuration the browser was started with, and that the SEB version has the JavaScript API (3.4+ on Windows).
* **present but refused**: the hash was made for a different address or key. The common causes are
  a wrong or stale Config Key (it changes with any setting), `http` against `https` or a different port in the Start
  URL, and a proxy that changes the address (the server reads `X-Forwarded-Proto` and `X-Forwarded-Host`; make the
  proxy send them). Compare the logged addresses with the Start URL.

## Tests that exist

* Backend: `SafeBrowserCheckTest` (the hash and the headers), `SafeBrowserAccessTest` (the interceptor),
  `SingleSessionTest` (open once, reopen, the key never goes to students).
* Frontend: `SafeExamBrowserTest.spec.ts`, `AvailableQuizzesViewTest.spec.ts`, `ShowQuizAnswersDialogTest.spec.ts`,
  `StatementQuizTimerTest.spec.ts`, `SpeedReportDialogTest.spec.ts`.
* `fraud-service/tests/test_speed.py` for the answer-times rules.

They test the algorithm and the screens, not a real SEB.
