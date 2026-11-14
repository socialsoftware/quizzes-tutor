// The Safe Exam Browser proves to the server that it is the browser set up for a quiz with the Config
// Key of its setup, hashed with the address of the page. A page of the Tutor makes its calls from
// script, so it reads the proof from the browser's JavaScript API and sends it with every call.

interface SebSecurity {
  configKey?: string;
  updateKeys?: (callback?: () => void) => void;
}

// The keys the browser gave for an address: they only prove the address they were made for
let proof: { pageUrl: string; configKey: string } | null = null;

const security = (): SebSecurity | undefined => (window as any).SafeExamBrowser?.security;

const currentPage = () => window.location.href.split('#')[0];

const remember = () => {
  const configKey = security()?.configKey;
  proof = configKey ? { pageUrl: currentPage(), configKey } : null;
};

/** True inside the Safe Exam Browser (a browser that has its JavaScript API). */
export const isSafeExamBrowser = (): boolean => !!security();

/** Asks the browser for the keys of the page being shown now; call it when the page changes. */
export const refreshSafeExamBrowserKeys = (): Promise<void> =>
  new Promise((resolve) => {
    const seb = security();
    if (!seb) return resolve();
    if (!seb.updateKeys) {
      remember();
      return resolve();
    }
    try {
      seb.updateKeys(() => {
        remember();
        resolve();
      });
    } catch {
      remember();
      resolve();
    }
  });

/** The headers that go with every call: empty outside the Safe Exam Browser. */
export const safeExamBrowserHeaders = (): Record<string, string> =>
  proof ? { 'X-SEB-Page-Url': proof.pageUrl, 'X-SEB-Config-Key-Hash': proof.configKey } : {};
