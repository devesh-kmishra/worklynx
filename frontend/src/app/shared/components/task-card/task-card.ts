import { Component, input, output, signal } from '@angular/core';
import { Task, TaskPriority, TaskStatus } from '../../../core/models/task.model';

@Component({
  selector: 'app-task-card',
  imports: [],
  templateUrl: './task-card.html',
  styleUrl: './task-card.css',
})
export class TaskCard {
  task = input.required<Task>();

  editable = input(true);

  edit = output<Task>();
  delete = output<Task>();
  statusChange = output<{ task: Task; status: TaskStatus }>();

  menuOpen = signal(false);

  readonly statuses: { value: TaskStatus; label: string }[] = [
    { value: 'TODO', label: 'To Do' },
    { value: 'IN_PROGRESS', label: 'In Progress' },
    { value: 'DONE', label: 'Done' },
  ];

  toggleMenu(): void {
    this.menuOpen.update((open) => !open);
  }

  closeMenu(): void {
    this.menuOpen.set(false);
  }

  onEdit(): void {
    this.closeMenu();
    this.edit.emit(this.task());
  }

  onDelete(): void {
    this.closeMenu();
    this.delete.emit(this.task());
  }

  changeStatus(status: TaskStatus): void {
    this.closeMenu();

    if (status === this.task().status) return;

    this.statusChange.emit({ task: this.task(), status });
  }

  priorityLabel(priority: TaskPriority): string {
    switch (priority) {
      case 'HIGH':
        return 'High';
      case 'MEDIUM':
        return 'Medium';
      case 'LOW':
        return 'Low';
    }
  }

  priorityClass(priority: TaskPriority): string {
    switch (priority) {
      case 'HIGH':
        return 'bg-red-100 text-red-700';
      case 'MEDIUM':
        return 'bg-amber-100 text-amber-700';
      case 'LOW':
        return 'bg-emerald-100 text-emerald-700';
    }
  }

  statusLabel(status: TaskStatus): string {
    switch (status) {
      case 'TODO':
        return 'To Do';
      case 'IN_PROGRESS':
        return 'In Progress';
      case 'DONE':
        return 'Done';
    }
  }

  statusClass(status: TaskStatus): string {
    switch (status) {
      case 'TODO':
        return 'bg-slate-100 text-slate-700';
      case 'IN_PROGRESS':
        return 'bg-blue-100 text-blue-700';
      case 'DONE':
        return 'bg-emerald-100 text-emerald-700';
    }
  }

  formatDueDate(dueDate: string | null): string {
    if (!dueDate) return '';

    const date = new Date(`${dueDate}T00:00:00`);
    const today = new Date();
    const tomorrow = new Date();

    today.setHours(0, 0, 0, 0);
    tomorrow.setHours(0, 0, 0, 0);
    tomorrow.setDate(tomorrow.getDate() + 1);

    if (date.getTime() === today.getTime()) return 'Today';

    if (date.getTime() === tomorrow.getTime()) return 'Tomorrow';

    return date.toLocaleDateString('en-IN', {
      day: 'numeric',
      month: 'short',
      year: 'numeric',
    });
  }

  isOverdue(task: Task): boolean {
    if (!task.dueDate || task.status === 'DONE') return false;

    const dueDate = new Date(`${task.dueDate}T00:00:00`);
    const today = new Date();

    today.setHours(0, 0, 0, 0);

    return dueDate < today;
  }
}
