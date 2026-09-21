import { Component, input, output, signal } from '@angular/core';
import { Task, TaskStatus } from '../../../core/models/task.model';
import { TaskCard } from '../../../shared/components/task-card/task-card';

@Component({
  selector: 'app-kanban-board',
  imports: [TaskCard],
  templateUrl: './kanban-board.html',
  styleUrl: './kanban-board.css',
})
export class KanbanBoard {
  tasks = input.required<Task[]>();

  edit = output<Task>();
  delete = output<Task>();
  statusChange = output<{ task: Task; status: TaskStatus }>();

  readonly columns: { status: TaskStatus; title: string }[] = [
    { status: 'TODO', title: 'To Do' },
    { status: 'IN_PROGRESS', title: 'In Progress' },
    { status: 'DONE', title: 'Done' },
  ];

  draggedTask = signal<Task | null>(null);

  tasksForColumn(status: TaskStatus): Task[] {
    return this.tasks().filter((task) => task.status === status);
  }

  startDrag(task: Task): void {
    this.draggedTask.set(task);
  }

  allowDrop(event: DragEvent): void {
    event.preventDefault();
  }

  dropTask(event: DragEvent, status: TaskStatus): void {
    event.preventDefault();

    const task = this.draggedTask();

    if (!task || task.status === status) {
      this.draggedTask.set(null);
      return;
    }

    this.statusChange.emit({ task, status });

    this.draggedTask.set(null);
  }

  onEdit(task: Task): void {
    this.edit.emit(task);
  }

  onDelete(task: Task): void {
    this.delete.emit(task);
  }

  onStatusChange(event: { task: Task; status: TaskStatus }): void {
    this.statusChange.emit(event);
  }
}
