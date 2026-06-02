import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { CurrencyDirective } from './currency.directive';

@Component({
  standalone: true,
  imports: [ReactiveFormsModule, CurrencyDirective],
  template: `<input appCurrency [formControl]="control" />`,
})
class HostComponent {
  public readonly control = new FormControl<number | null>(null);
}

describe('CurrencyDirective', () => {
  let fixture: ComponentFixture<HostComponent>;
  let input: HTMLInputElement;

  beforeEach(() => {
    TestBed.configureTestingModule({ imports: [HostComponent] });
    fixture = TestBed.createComponent(HostComponent);
    fixture.detectChanges();
    input = fixture.nativeElement.querySelector('input') as HTMLInputElement;
  });

  function blurWith(value: string): void {
    input.value = value;
    input.dispatchEvent(new Event('blur'));
    fixture.detectChanges();
  }

  it('formats a plain number to two decimals on blur', () => {
    blurWith('1234.5');
    expect(input.value).toBe('1234.50');
    expect(fixture.componentInstance.control.value).toBe(1234.5);
  });

  it('accepts a comma decimal separator', () => {
    blurWith('99,9');
    expect(input.value).toBe('99.90');
    expect(fixture.componentInstance.control.value).toBe(99.9);
  });

  it('leaves empty input untouched', () => {
    blurWith('');
    expect(input.value).toBe('');
    expect(fixture.componentInstance.control.value).toBeNull();
  });

  it('leaves non-numeric input untouched', () => {
    blurWith('abc');
    expect(input.value).toBe('abc');
  });
});
