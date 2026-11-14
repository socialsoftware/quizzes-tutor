import { afterEach, beforeEach, describe, expect, test, vi } from 'vitest';
import { nextTick, ref, watch } from 'vue';
import StatementQuiz from '@/models/statement/StatementQuiz';

const quiz = (timeToSubmission: number | null, timeToAvailability: number | null = null) =>
  new StatementQuiz({
    id: 1,
    courseName: 'Course',
    quizAnswerId: 1,
    title: 'Quiz',
    qrCodeOnly: false,
    oneWay: false,
    timed: true,
    availableDate: '2030-01-01T10:00:00Z',
    conclusionDate: '2030-01-01T11:00:00Z',
    questionOrder: 0,
    timeToAvailability,
    timeToSubmission,
    questions: [],
    answers: [],
  } as any);

describe('StatementQuiz timer', () => {
  beforeEach(() => {
    vi.useFakeTimers();
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  // The views keep the quiz in a ref, which is what the page watches and shows
  test('the time left changes in what the page sees, second by second', async () => {
    const shown = ref(quiz(65_000));

    await vi.advanceTimersByTimeAsync(3000);

    expect(shown.value.timeToSubmission).toBe(62_000);
  });

  test('a watcher on the time left is told when it changes and when it ends', async () => {
    const shown = ref(quiz(2000));
    const seen: Array<number | null | undefined> = [];
    watch(
      () => shown.value.timeToSubmission,
      (value) => seen.push(value)
    );

    await vi.advanceTimersByTimeAsync(3000);
    await nextTick();

    expect(seen[seen.length - 1]).toBe(0);
  });

  test('the time to the start of a quiz counts down the same way', async () => {
    const shown = ref(quiz(null, 5000));
    const seen: Array<number | null | undefined> = [];
    watch(
      () => shown.value.timeToAvailability,
      (value) => seen.push(value)
    );

    await vi.advanceTimersByTimeAsync(6000);
    await nextTick();

    expect(shown.value.timeToAvailability).toBe(0);
    expect(seen[seen.length - 1]).toBe(0);
  });

  test('the timer stops by itself when the time is over', async () => {
    const clear = vi.spyOn(window, 'clearInterval');
    quiz(1000);

    await vi.advanceTimersByTimeAsync(3000);

    expect(clear).toHaveBeenCalled();
  });
});
