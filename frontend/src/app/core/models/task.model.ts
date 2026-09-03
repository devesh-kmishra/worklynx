export type TaskStatus = 'TODO' | 'IN_PROGRESS' | 'DONE';

export type TaskPriority = 'LOW' | 'MEDIUM' | 'HIGH';

export type TaskSort = 'RECENTLY_CREATED' | 'DUE_DATE' | 'PRIORITY';

export interface Task {
  id: number;
  title: string;
  description: string | null;

  status: TaskStatus;
  priority: TaskPriority;

  dueDate: string | null;

  createdAt: string;
  updatedAt: string;

  projectId?: number | null;
  assignedToId?: number | null;
}

export interface CreateTaskRequest {
  title: string;
  description?: string;
  status?: TaskStatus;
  priority?: TaskPriority;
  dueDate?: string;
  projectId?: number;
  assignedToId?: number;
}

export interface UpdateTaskRequest {
  title?: string;
  description?: string;
  status?: TaskStatus;
  priority?: TaskPriority;
  dueDate?: string;
  assignedToId?: number;
}

export interface TaskPage {
  content: Task[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}
