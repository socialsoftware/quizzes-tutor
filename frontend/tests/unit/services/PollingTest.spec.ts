import { afterEach, beforeEach, describe, expect, test, vi } from 'vitest';
import { createPoller } from '@/services/Polling';

describe('createPoller', () => {
  beforeEach(() => vi.useFakeTimers());
  afterEach(() => vi.useRealTimers());

  test('keeps running while the task asks for it and stops when it does not', async () => {
    const task = vi.fn().mockResolvedValueOnce(true).mockResolvedValueOnce(true).mockResolvedValue(false);
    createPoller(task, 1000).start();

    await vi.advanceTimersByTimeAsync(10_000);

    expect(task).toHaveBeenCalledTimes(3);
  });

  test('a failing run does not end the loop', async () => {
    const task = vi.fn().mockRejectedValueOnce(new Error('offline')).mockResolvedValue(false);
    createPoller(task, 1000).start();

    await vi.advanceTimersByTimeAsync(5_000);

    expect(task).toHaveBeenCalledTimes(2);
  });

  test('stop cancels the next run', async () => {
    const task = vi.fn().mockResolvedValue(true);
    const poller = createPoller(task, 1000);
    poller.start();
    await vi.advanceTimersByTimeAsync(1000);
    poller.stop();

    await vi.advanceTimersByTimeAsync(10_000);

    expect(task).toHaveBeenCalledTimes(1);
  });

  test('never starts a second loop while one is running', async () => {
    const task = vi.fn().mockResolvedValue(false);
    const poller = createPoller(task, 1000);
    poller.start();
    poller.start();

    await vi.advanceTimersByTimeAsync(5_000);

    expect(task).toHaveBeenCalledTimes(1);
  });

  test('a slow run is not overlapped by the next one', async () => {
    let running = 0;
    let overlapped = false;
    const task = vi.fn(async () => {
      running++;
      overlapped = overlapped || running > 1;
      await new Promise((resolve) => setTimeout(resolve, 3000));
      running--;
      return true;
    });
    const poller = createPoller(task, 1000);
    poller.start();

    await vi.advanceTimersByTimeAsync(20_000);
    poller.stop();

    expect(overlapped).toBe(false);
    expect(task.mock.calls.length).toBeGreaterThan(1);
  });
});
