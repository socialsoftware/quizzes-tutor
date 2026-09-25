<template>
  <!-- dp--theme-light defines the --dp-* variables the input's border and colours use -->
  <div :id="id" class="dtp-side-by-side dp--theme-light" ref="rootEl">
    <div class="dp--input-wrap">
      <span class="dp--input-icon dp--input-icons">
        <i class="mdi mdi-calendar"></i>
      </span>
      <input
        type="text"
        class="dp--input dp--input-reg dp--input-icon-pad"
        readonly
        :placeholder="placeholder"
        :value="displayValue"
        @click="toggleOpen"
      />
    </div>
    <Teleport to="body">
      <div
        v-if="open"
        ref="menuEl"
        class="dtp-menu dp--menu dp--menu-index dp--theme-light"
        :style="menuStyle"
      >
        <div class="dtp-menu-content">
          <div class="dtp-panel">
            <VueDatePicker
              inline
              auto-apply
              :enable-time-picker="false"
              :locale="locale"
              v-model="dateOnly"
            />
          </div>
          <div class="dtp-panel">
            <VueDatePicker
              inline
              auto-apply
              time-picker
              :locale="locale"
              v-model="timeOnly"
            />
          </div>
        </div>
        <div class="dp--action-row">
          <div class="dp--action-buttons">
            <button
              type="button"
              class="dp--action-button dp--action-cancel"
              @click="cancel"
            >
              Cancel
            </button>
            <button
              type="button"
              class="dp--action-button dp--action-select"
              @click="confirm"
            >
              Select
            </button>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, nextTick, onBeforeUnmount } from 'vue';
import { VueDatePicker } from '@vuepic/vue-datepicker';
import '@vuepic/vue-datepicker/dist/main.css';

interface TimeValue {
  hours: number;
  minutes: number;
  seconds: number;
}

const props = defineProps<{
  modelValue?: string | null;
  id?: string;
  placeholder?: string;
  locale?: any;
}>();

const emit = defineEmits(['update:modelValue']);

const MOBILE_BREAKPOINT = 600;
const VIEWPORT_MARGIN = 8;
const INPUT_GAP = 4;

const open = ref(false);
const rootEl = ref<HTMLElement | null>(null);
const menuEl = ref<HTMLElement | null>(null);
const menuStyle = ref<Record<string, string>>({});
const dateOnly = ref<Date>(new Date());
const timeOnly = ref<TimeValue>({ hours: 0, minutes: 0, seconds: 0 });

const pad = (n: number) => n.toString().padStart(2, '0');

const displayValue = computed(() => {
  if (!props.modelValue) return '';
  const d = new Date(props.modelValue);
  if (isNaN(d.getTime())) return '';
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`;
});

// The menu is teleported to <body> so it can never be clipped by a dialog or
// card with overflow hidden; that means its position has to be computed and
// clamped to the viewport by hand.
const positionMenu = () => {
  const root = rootEl.value;
  const menu = menuEl.value;
  if (!root || !menu) return;

  const anchor = root.getBoundingClientRect();
  const menuWidth = menu.offsetWidth;
  const menuHeight = menu.offsetHeight;
  const viewportWidth = window.innerWidth;
  const viewportHeight = window.innerHeight;

  let left: number;
  let top: number;

  if (viewportWidth <= MOBILE_BREAKPOINT) {
    left = Math.max(VIEWPORT_MARGIN, (viewportWidth - menuWidth) / 2);
    top = Math.max(VIEWPORT_MARGIN, (viewportHeight - menuHeight) / 2);
  } else {
    left = Math.min(
      Math.max(VIEWPORT_MARGIN, anchor.left),
      viewportWidth - menuWidth - VIEWPORT_MARGIN
    );
    left = Math.max(VIEWPORT_MARGIN, left);

    top = anchor.bottom + INPUT_GAP;
    if (top + menuHeight > viewportHeight - VIEWPORT_MARGIN) {
      const above = anchor.top - menuHeight - INPUT_GAP;
      top =
        above >= VIEWPORT_MARGIN
          ? above
          : Math.max(VIEWPORT_MARGIN, viewportHeight - menuHeight - VIEWPORT_MARGIN);
    }
  }

  menuStyle.value = { left: `${Math.round(left)}px`, top: `${Math.round(top)}px` };
};

const onDocClick = (event: MouseEvent) => {
  const target = event.target as Node;
  const insideAnchor = rootEl.value?.contains(target);
  const insideMenu = menuEl.value?.contains(target);
  if (!insideAnchor && !insideMenu) close();
};

const onViewportChange = () => positionMenu();

const addListeners = () => {
  document.addEventListener('click', onDocClick, true);
  window.addEventListener('resize', onViewportChange);
  window.addEventListener('scroll', onViewportChange, true);
};

const removeListeners = () => {
  document.removeEventListener('click', onDocClick, true);
  window.removeEventListener('resize', onViewportChange);
  window.removeEventListener('scroll', onViewportChange, true);
};

const close = () => {
  open.value = false;
  removeListeners();
};

const toggleOpen = async () => {
  if (open.value) {
    close();
    return;
  }

  const base = props.modelValue ? new Date(props.modelValue) : new Date();
  dateOnly.value = isNaN(base.getTime()) ? new Date() : base;
  timeOnly.value = {
    hours: dateOnly.value.getHours(),
    minutes: dateOnly.value.getMinutes(),
    seconds: 0,
  };

  // Hide it for the first frame so the pre-positioning flash isn't visible.
  menuStyle.value = { left: '-9999px', top: '-9999px' };
  open.value = true;
  addListeners();
  await nextTick();
  positionMenu();
};

const confirm = () => {
  const result = new Date(dateOnly.value);
  result.setHours(
    timeOnly.value.hours,
    timeOnly.value.minutes,
    timeOnly.value.seconds || 0,
    0
  );
  emit('update:modelValue', result.toISOString());
  close();
};

const cancel = () => close();

onBeforeUnmount(removeListeners);
</script>

<style lang="scss" scoped>
.dtp-side-by-side {
  position: relative;
  width: 100%;
}

.dp--input-wrap {
  position: relative;
}

.dtp-menu {
  position: fixed;
  z-index: 99999;
  width: max-content;
  max-width: calc(100vw - 16px);
  max-height: calc(100vh - 16px);
  overflow-y: auto;
  box-sizing: border-box;
}

.dtp-menu-content {
  display: flex;
  flex-wrap: wrap;
  align-items: flex-start;
}

// Some browsers (Firefox) stretch flex children to fill the row when the
// library's own internal wrapper sets width:100%, which pushes the time panel
// onto a collapsed second line. Pin each panel to the library's menu width.
.dtp-panel {
  flex: 0 0 260px;
  width: 260px;
  min-width: 0;
}

// The date-only side doesn't need its own "open time picker" toggle —
// the time picker is already shown side by side.
.dtp-menu-content :deep([data-test-id='open-time-picker-btn']) {
  display: none;
}
</style>
