import { afterEach, beforeEach, describe, expect, test, vi } from 'vitest';
import { isSafeExamBrowser, refreshSafeExamBrowserKeys, safeExamBrowserHeaders } from '@/services/SafeExamBrowser';

describe('SafeExamBrowser', () => {
  afterEach(() => {
    delete (window as any).SafeExamBrowser;
  });

  beforeEach(async () => {
    // forget what an earlier test remembered
    delete (window as any).SafeExamBrowser;
    await refreshSafeExamBrowserKeys();
  });

  test('outside the Safe Exam Browser nothing is sent', async () => {
    await refreshSafeExamBrowserKeys();

    expect(isSafeExamBrowser()).toBe(false);
    expect(safeExamBrowserHeaders()).toEqual({});
  });

  test('inside it, the keys of the page are asked for and sent with the address they were made for', async () => {
    const security = {
      configKey: 'old',
      updateKeys: vi.fn((callback?: () => void) => {
        security.configKey = 'abc123';
        callback?.();
      }),
    };
    (window as any).SafeExamBrowser = { security };

    await refreshSafeExamBrowserKeys();

    expect(isSafeExamBrowser()).toBe(true);
    expect(security.updateKeys).toHaveBeenCalled();
    expect(safeExamBrowserHeaders()).toEqual({
      'X-SEB-Page-Url': window.location.href.split('#')[0],
      'X-SEB-Config-Key-Hash': 'abc123',
    });
  });

  test('a browser that cannot update its keys gives the ones it has', async () => {
    (window as any).SafeExamBrowser = { security: { configKey: 'fixed' } };

    await refreshSafeExamBrowserKeys();

    expect(safeExamBrowserHeaders()['X-SEB-Config-Key-Hash']).toBe('fixed');
  });

  test('a browser whose update fails does not stop the page', async () => {
    (window as any).SafeExamBrowser = {
      security: {
        configKey: 'k',
        updateKeys: () => {
          throw new Error('not now');
        },
      },
    };

    await expect(refreshSafeExamBrowserKeys()).resolves.toBeUndefined();
    expect(safeExamBrowserHeaders()['X-SEB-Config-Key-Hash']).toBe('k');
  });
});
