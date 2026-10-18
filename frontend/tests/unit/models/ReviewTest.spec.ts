import { describe, expect, test } from 'vitest';
import Review from '@/models/management/Review';

describe('Review.statusOptionFor', () => {
  const approve = { title: 'Approve', key: 'APPROVE', color: 'green' };

  test('knows the option Vuetify 4 puts in the selection slot', () => {
    expect(Review.statusOptionFor(approve)).toEqual(approve);
    expect(Review.statusOptionFor({ title: 'Request Changes', key: 'REQUEST_CHANGES', color: 'yellow' }).title).toBe(
      'Request Changes'
    );
  });

  test('knows the wrappers of other versions and a plain key', () => {
    expect(Review.statusOptionFor({ raw: approve }).title).toBe('Approve');
    expect(Review.statusOptionFor({ value: 'REJECT' }).title).toBe('Reject');
    expect(Review.statusOptionFor('COMMENT').title).toBe('Comment');
  });

  test('every option can be shown with its own title and color', () => {
    Review.statusOptions.forEach((option) => {
      expect(Review.statusOptionFor(option).title).toBe(option.title);
      expect(Review.statusOptionFor(option).color).toBe(option.color);
    });
  });
});
