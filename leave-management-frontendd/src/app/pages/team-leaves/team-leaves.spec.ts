import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TeamLeaves } from './team-leaves';

describe('TeamLeaves', () => {
  let component: TeamLeaves;
  let fixture: ComponentFixture<TeamLeaves>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TeamLeaves]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TeamLeaves);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
