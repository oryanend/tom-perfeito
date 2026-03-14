import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MusicsPageComponent } from './musics-page.component';

describe('MusicsPageComponent', () => {
  let component: MusicsPageComponent;
  let fixture: ComponentFixture<MusicsPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MusicsPageComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MusicsPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
