import { Component, effect, inject, input, output, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { TaskService } from '../../../core/services/task';
import {
  CreateTaskRequest,
  Task,
  TaskPriority,
  TaskStatus,
  UpdateTaskRequest,
} from '../../../core/models/task.model';

@Component({
  selector: 'app-task-form',
  imports: [ReactiveFormsModule],
  templateUrl: './task-form.html',
  styleUrl: './task-form.css',
})
export class TaskForm {
  private fb = inject(FormBuilder);
  private taskService = inject(TaskService);

  task = input<Task | null>(null);

  taskCreated = output<Task>();
  taskUpdated = output<Task>();
  cancelled = output<void>();

  loading = signal(false);
  error = signal('');

  taskForm = this.fb.nonNullable.group({
    title: ['', [Validators.required, Validators.maxLength(200)]],
    description: ['', Validators.maxLength(5000)],
    status: this.fb.nonNullable.control<TaskStatus>('TODO'),
    priority: this.fb.nonNullable.control<TaskPriority>('MEDIUM'),
    dueDate: [''],
  });

  constructor() {
    effect(() => {
      const task = this.task();

      if (!task) return;

      this.taskForm.patchValue({
        title: task.title,
        description: task.description ?? '',
        status: task.status,
        priority: task.priority,
        dueDate: task.dueDate ?? '',
      });
    });
  }

  get isEditMode(): boolean {
    return this.task() !== null;
  }

  submit() {
    if (this.taskForm.invalid) {
      this.taskForm.markAllAsTouched();
      return;
    }

    this.loading.set(true);
    this.error.set('');

    const value = this.taskForm.getRawValue();

    if (this.task()) {
      this.updateTask(value);
    } else {
      this.createTask(value);
    }
  }

  private createTask(value: ReturnType<typeof this.taskForm.getRawValue>) {
    const request: CreateTaskRequest = {
      title: value.title.trim(),
      description: value.description.trim() || undefined,
      status: value.status,
      priority: value.priority,
      dueDate: value.dueDate || undefined,
    };

    this.taskService.createTask(request).subscribe({
      next: (task) => {
        this.loading.set(false);
        this.taskCreated.emit(task);
      },
      error: (error) => {
        this.handleError(error);
      },
    });
  }

  private updateTask(value: ReturnType<typeof this.taskForm.getRawValue>) {
    const task = this.task();

    if (!task) return;

    const request: UpdateTaskRequest = {
      title: value.title.trim(),
      description: value.description.trim() || undefined,
      status: value.status,
      priority: value.priority,
      dueDate: value.dueDate || undefined,
    };

    this.taskService.updateTask(task.id, request).subscribe({
      next: (updatedTask) => {
        this.loading.set(false);
        this.taskUpdated.emit(updatedTask);
      },
      error: (error) => {
        this.handleError(error);
      },
    });
  }

  private handleError(error: any) {
    this.loading.set(false);
    this.error.set(error?.error?.message ?? 'Unable to save the task.');
  }

  cancel() {
    if (!this.loading()) {
      this.cancelled.emit();
    }
  }
}
