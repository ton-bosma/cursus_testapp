import { Directive, ElementRef, HostListener, inject } from '@angular/core';
import { NgControl } from '@angular/forms';

/**
 * Normalizes a numeric amount input to two decimals on blur.
 *
 * - Accepts both comma and dot as the decimal separator while typing.
 * - On blur the value is parsed and re-rendered with exactly two decimals
 *   (e.g. `1234.5` → `1234.50`), keeping a `.` as the canonical separator.
 * - Empty / unparseable input is left untouched so it can be cleared.
 * - When applied to a reactive-forms control, the normalized numeric value is
 *   written back through the control; otherwise only the input element value
 *   is updated.
 *
 * Usage: `<input appCurrency formControlName="inkoopInstr">`
 */
@Directive({
  selector: '[appCurrency]',
  standalone: true,
})
export class CurrencyDirective {
  private readonly _el = inject<ElementRef<HTMLInputElement>>(ElementRef);
  private readonly _control = inject(NgControl, { optional: true });

  @HostListener('blur')
  protected onBlur(): void {
    const raw = this._el.nativeElement.value;
    const normalized = this._normalize(raw);
    if (normalized === null) {
      return;
    }

    // Push the numeric value back through the bound control first; this makes
    // Angular's value accessor write the raw number into the DOM, so we set the
    // formatted (2-decimal) display string afterwards to keep it visible.
    if (this._control?.control) {
      this._control.control.setValue(Number(normalized), { emitEvent: false });
    }
    this._el.nativeElement.value = normalized;
  }

  /**
   * Parses a free-form amount string and returns it formatted to two decimals,
   * or `null` when the input is empty or not a number.
   */
  private _normalize(raw: string): string | null {
    const trimmed = raw.trim();
    if (trimmed === '') {
      return null;
    }

    const parsed = Number(trimmed.replace(',', '.'));
    if (Number.isNaN(parsed)) {
      return null;
    }

    return parsed.toFixed(2);
  }
}
