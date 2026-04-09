import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MusicCreatePageComponent } from './music-create-page.component';

describe('MusicCreatePageComponent', () => {
  let component: MusicCreatePageComponent;
  let fixture: ComponentFixture<MusicCreatePageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MusicCreatePageComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(MusicCreatePageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
