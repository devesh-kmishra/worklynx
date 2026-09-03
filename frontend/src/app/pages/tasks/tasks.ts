import { Component, inject, signal } from '@angular/core';
import { TaskService } from '../../core/services/task';
import { Task, TaskPriority, TaskSort, TaskStatus } from '../../core/models/task.model';
import { TaskForm } from '../../shared/components/task-form/task-form';

type TaskFilter = 'ALL' | 'TODO' | 'IN_PROGRESS' | 'DONE';

@Component({
  selector: 'app-tasks',
  imports: [TaskForm],
  templateUrl: './tasks.html',
  styleUrl: './tasks.css',
})
export class Tasks {
  private taskService = inject(TaskService);

  tasks = signal<Task[]>([]);

  editingTask = signal<Task | null>(null);
  taskToDelete = signal<Task | null>(null);
  openMenuTaskId = signal<number | null>(null);

  loading = signal(true);
  error = signal('');

  activeFilter = signal<TaskFilter>('ALL');
  sort = signal<TaskSort>('RECENTLY_CREATED');
  page = signal(0);
  pageSize = 10;
  totalPages = signal(0);
  totalElements = signal(0);

  showTaskForm = signal(false);
  deleting = signal(false);

  ngOnInit() {
    this.loadTasks();
  }

  loadTasks() {
    this.loading.set(true);
    this.error.set('');

    const filter = this.activeFilter();

    const status = filter === 'ALL' ? undefined : filter;

    this.taskService.getTasks(this.page(), this.pageSize, status, this.sort()).subscribe({
      next: (response) => {
        this.tasks.set(response.content);
        this.totalPages.set(response.totalPages);
        this.totalElements.set(response.totalElements);
        this.loading.set(false);
      },
      error: (error) => {
        this.error.set(error?.error?.message ?? 'Unable to load your tasks.');
        this.loading.set(false);
      },
    });
  }

  setFilter(filter: TaskFilter) {
    this.activeFilter.set(filter);
    this.page.set(0);
    this.closeMenu();
    this.loadTasks();
  }

  setSort(sort: TaskSort) {
    this.sort.set(sort);
    this.page.set(0);
    this.closeMenu();
    this.loadTasks();
  }

  previousPage() {
    if (this.page() === 0) return;

    this.page.update((page) => page - 1);
    this.loadTasks();
  }

  nextPage() {
    if (this.page() >= this.totalPages() - 1) return;

    this.page.update((page) => page + 1);
    this.loadTasks();
  }

  goToPage(page: number) {
    if (page < 0 || page >= this.totalPages() || page === this.page()) return;

    this.page.set(page);
    this.loadTasks();
  }

  updateTaskStatus(task: Task, status: TaskStatus) {
    if (task.status === status) return;

    this.taskService.updateTask(task.id, { status }).subscribe({
      next: (updatedTask) => {
        this.tasks.update((tasks) =>
          tasks.map((currentTask) =>
            currentTask.id === updatedTask.id ? updatedTask : currentTask,
          ),
        );
      },
      error: (error) => {
        this.error.set(error?.error?.message ?? 'Unable to update the task.');
      },
    });
  }

  toggleMenu(taskId: number) {
    this.openMenuTaskId.update((current) => (current === taskId ? null : taskId));
  }

  closeMenu() {
    this.openMenuTaskId.set(null);
  }

  openTaskForm() {
    this.editingTask.set(null);
    this.closeMenu();
    this.showTaskForm.set(true);
  }

  closeTaskForm() {
    this.showTaskForm.set(false);
  }

  handleTaskCreated(task: Task) {
    // If on first page, show new task immediately
    if (this.page() === 0) {
      this.tasks.update((tasks) => [task, ...tasks]);

      // Keep page size consistent
      this.tasks.update((tasks) => tasks.slice(0, this.pageSize));
    } else {
      // Reload to show newly-created task
      this.loadTasks();
    }

    this.showTaskForm.set(false);
  }

  openEditTask(task: Task) {
    this.showTaskForm.set(false);
    this.closeMenu();
    this.editingTask.set(task);
  }

  closeEditTask() {
    this.editingTask.set(null);
  }

  handleTaskUpdated(updatedTask: Task) {
    this.tasks.update((tasks) =>
      tasks.map((task) => (task.id === updatedTask.id ? updatedTask : task)),
    );

    this.editingTask.set(null);
  }

  confirmDelete(task: Task) {
    this.closeMenu();
    this.taskToDelete.set(task);
  }

  cancelDelete() {
    if (!this.deleting()) {
      this.taskToDelete.set(null);
    }
  }

  deleteTask() {
    const task = this.taskToDelete();

    if (!task) return;

    this.deleting.set(true);

    this.taskService.deleteTask(task.id).subscribe({
      next: () => {
        this.tasks.update((tasks) => tasks.filter((currentTask) => currentTask.id !== task.id));

        this.totalElements.update((total) => Math.max(0, total - 1));

        // If we deleted the last task on the current page, go back one page
        if (this.tasks().length === 0 && this.page() > 0) {
          this.page.update((page) => page - 1);
          this.loadTasks();
        }

        this.taskToDelete.set(null);
        this.deleting.set(false);
      },
      error: (error) => {
        this.deleting.set(false);
        this.error.set(error?.error?.message ?? 'Unable to delete the task.');
      },
    });
  }

  getPriorityClass(priority: TaskPriority): string {
    switch (priority) {
      case 'HIGH':
        return 'bg-red-500';

      case 'MEDIUM':
        return 'bg-amber-500';

      case 'LOW':
        return 'bg-emerald-500';
    }
  }

  getPriorityLabel(priority: TaskPriority): string {
    switch (priority) {
      case 'HIGH':
        return 'High';

      case 'MEDIUM':
        return 'Medium';

      case 'LOW':
        return 'Low';
    }
  }

  getStatusClass(status: TaskStatus): string {
    switch (status) {
      case 'TODO':
        return 'bg-slate-100 text-slate-600';

      case 'IN_PROGRESS':
        return 'bg-blue-50 text-blue-700';

      case 'DONE':
        return 'bg-emerald-50 text-emerald-700';
    }
  }

  getStatusLabel(status: TaskStatus): string {
    switch (status) {
      case 'TODO':
        return 'To Do';

      case 'IN_PROGRESS':
        return 'In Progress';

      case 'DONE':
        return 'Done';
    }
  }

  isOverdue(task: Task): boolean {
    if (!task.dueDate || task.status === 'DONE') {
      return false;
    }

    return task.dueDate < this.today();
  }

  formatDueDate(task: Task): string {
    if (!task.dueDate) return '';

    if (this.isOverdue(task)) {
      return 'Overdue';
    }

    const today = this.today();

    if (task.dueDate === today) {
      return 'Today';
    }

    const tomorrow = this.addDays(today, 1);

    if (task.dueDate === tomorrow) {
      return 'Tomorrow';
    }

    const date = new Date(`${task.dueDate}T00:00:00`);

    return new Intl.DateTimeFormat('en-IN', {
      month: 'short',
      day: 'numeric',
      year: date.getFullYear() !== new Date().getFullYear() ? 'numeric' : undefined,
    }).format(date);
  }

  private today(): string {
    const date = new Date();

    return [
      date.getFullYear(),
      String(date.getMonth() + 1).padStart(2, '0'),
      String(date.getDate()).padStart(2, '0'),
    ].join('-');
  }

  private addDays(dateString: string, days: number): string {
    const date = new Date(`${dateString}T00:00:00`);

    date.setDate(date.getDate() + days);

    return [
      date.getFullYear(),
      String(date.getMonth() + 1).padStart(2, '0'),
      String(date.getDate()).padStart(2, '0'),
    ].join('-');
  }

  getPageNumbers(): number[] {
    const total = this.totalPages();

    return Array.from({ length: total }, (_, index) => index);
  }
}
