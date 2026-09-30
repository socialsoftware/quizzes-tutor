export interface Poller {
  start: () => void;
  stop: () => void;
}

/**
 * Runs `task` every `intervalMs` until it answers false or `stop` is called. The next run
 * is only scheduled after the previous one finished, so a slow server is never hit twice at once.
 */
export function createPoller(task: () => Promise<boolean>, intervalMs: number): Poller {
  let timer: ReturnType<typeof setTimeout> | null = null;
  let running = false;

  const schedule = () => {
    timer = setTimeout(async () => {
      timer = null;
      let again: boolean;
      try {
        again = await task();
      } catch {
        // A failed poll must not end the loop: the next run may well succeed
        again = true;
      }
      if (running && again) schedule();
      else running = false;
    }, intervalMs);
  };

  return {
    start() {
      if (running) return;
      running = true;
      schedule();
    },
    stop() {
      running = false;
      if (timer !== null) {
        clearTimeout(timer);
        timer = null;
      }
    },
  };
}
