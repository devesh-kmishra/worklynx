import { inject, Injectable, signal } from '@angular/core';
import {
  CreateTaskRequest,
  Task,
  TaskPage,
  TaskSort,
  TaskStatus,
  UpdateTaskRequest,
} from '../models/task.model';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class TaskService {
  private http = inject(HttpClient);

  private apiUrl = `${environment.apiUrl}/tasks`;

  getTasks(
    page = 0,
    size = 10,
    status?: TaskStatus,
    sort: TaskSort = 'RECENTLY_CREATED',
  ): Observable<TaskPage> {
    let params = new HttpParams().set('page', page).set('size', size).set('sort', sort);

    if (status) {
      params = params.set('status', status);
    }

    return this.http.get<TaskPage>(this.apiUrl, {
      params,
      withCredentials: true,
    });
  }

  getBoardTasks(status?: TaskStatus): Observable<Task[]> {
    let params = new HttpParams();

    if (status) {
      params = params.set('status', status);
    }

    return this.http.get<Task[]>(`${this.apiUrl}/board`, {
      params,
      withCredentials: true,
    });
  }

  createTask(request: CreateTaskRequest): Observable<Task> {
    return this.http.post<Task>(this.apiUrl, request, {
      withCredentials: true,
    });
  }

  updateTask(taskId: number, request: UpdateTaskRequest): Observable<Task> {
    return this.http.patch<Task>(`${this.apiUrl}/${taskId}`, request, {
      withCredentials: true,
    });
  }

  deleteTask(taskId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${taskId}`, {
      withCredentials: true,
    });
  }
}
