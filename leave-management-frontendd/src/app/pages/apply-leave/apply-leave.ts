import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, AbstractControl,ValidationErrors ,ReactiveFormsModule } from '@angular/forms';
import { LeaveService } from '../../services/leave';
import { Router ,ActivatedRoute} from '@angular/router';


export function dateRangeValidator(group: AbstractControl): ValidationErrors | null {
  const startDate = group.get('startDate')?.value;
  const endDate = group.get('endDate')?.value;
  
  if (startDate && endDate && new Date(endDate) < new Date(startDate)) {
    return { dateRangeInvalid: true }; // Return error if end date is before start date
  }
  return null; // Return null if validation passes
}

@Component({
  selector: 'app-apply-leave',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './apply-leave.html',
  styleUrl: './apply-leave.scss'
})
export class ApplyLeaveComponent implements OnInit {
  leaveForm: FormGroup;
  leaveTypes = ['CASUAL', 'SICK', 'EARNED', 'MATERNITY', 'PATERNITY'];
  showFileUpload = false;
  selectedFile: File | null = null;
  successMessage = '';
  errorMessage = '';

    isEditMode = false;
  leaveIdToEdit: number | null = null;
  pageTitle = 'Apply for Leave';

  constructor(
    private fb: FormBuilder,
    private leaveService: LeaveService,
    private router: Router,
    private route: ActivatedRoute ,
    
  )  {
    this.leaveForm = this.fb.group({
      leaveType: ['', Validators.required],
      startDate: ['', [Validators.required, this.startDateValidator]], // Add custom validator
      endDate: ['', Validators.required],
      isHalfDay: [false],
      reason: ['', Validators.required],
      document: [null]
    }, { validators: dateRangeValidator }); // Add validator at the form group level
  }

   startDateValidator(control: AbstractControl): ValidationErrors | null {
    const selectedDate = new Date(control.value);
    const today = new Date();
    today.setHours(0, 0, 0, 0); // Normalize today's date to midnight

    if (selectedDate < today) {
      return { dateInPast: true };
    }
    return null;
  }

  ngOnInit(): void {

        this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.isEditMode = true;
        this.leaveIdToEdit = +id;
        this.pageTitle = 'Edit Leave Request';
        this.loadLeaveForEdit();
      }
    });
    // Subscribe to changes in the leaveType dropdown
    this.leaveForm.get('leaveType')?.valueChanges.subscribe(value => {
      this.showFileUpload = value === 'SICK';
    });

    // When half-day is checked, end date should be same as start date
    this.leaveForm.get('isHalfDay')?.valueChanges.subscribe(isHalfDay => {
      const startDateControl = this.leaveForm.get('startDate');
      const endDateControl = this.leaveForm.get('endDate');
      if (isHalfDay) {
        endDateControl?.setValue(startDateControl?.value);
        endDateControl?.disable();
      } else {
        endDateControl?.enable();
      }
    });
  }

    loadLeaveForEdit(): void {
    if (!this.leaveIdToEdit) return;
    this.leaveService.getLeaveById(this.leaveIdToEdit).subscribe(leaveData => {
      this.leaveForm.patchValue({
        leaveType: leaveData.leaveType,
        startDate: leaveData.startDate,
        endDate: leaveData.endDate,
        isHalfDay: leaveData.isHalfDay,
        reason: leaveData.reason
      });
    });
  }

  onFileSelected(event: Event): void {
    const element = event.currentTarget as HTMLInputElement;
    let fileList: FileList | null = element.files;
    if (fileList) {
      this.selectedFile = fileList[0];
    }
  }

         onSubmit(): void {
        if (this.leaveForm.invalid) {
          this.errorMessage = 'Please fill out all required fields correctly.';
          return;
        }

        // Reset messages
        this.errorMessage = '';
        this.successMessage = '';

        // --- THIS IS THE FIX ---
        // Use getRawValue() to include the value of the disabled endDate control
        const formValue = this.leaveForm.getRawValue();

        const applicationData = {
            ...formValue,
            document: this.selectedFile
        };

        if (this.isEditMode && this.leaveIdToEdit) {
            // Edit logic
            this.leaveService.editLeave(this.leaveIdToEdit, applicationData).subscribe({
              next: () => {
                this.successMessage = 'Leave updated successfully! Redirecting...';
                setTimeout(() => this.router.navigate(['/my-leaves']), 2000);
              },
              error: (err) => this.errorMessage = err.error.message || 'Failed to update leave.'
            });
        } else {
            // Create logic
            this.leaveService.applyForLeave(applicationData).subscribe({
              next: () => {
                this.successMessage = 'Leave applied successfully! Redirecting...';
                setTimeout(() => this.router.navigate(['/my-leaves']), 2000);
              },
              error: (err) => {
                  // Now we can see the detailed error message from the backend
                  this.errorMessage = err.error?.message || 'An unexpected error occurred.';
                  console.error(err);
              }
            });
        }
      }
  
}