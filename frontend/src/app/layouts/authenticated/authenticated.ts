import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Sidebar } from '../../shared/components/sidebar/sidebar';
import { Topbar } from '../../shared/components/topbar/topbar';

@Component({
  selector: 'app-authenticated',
  imports: [RouterOutlet, Sidebar, Topbar],
  templateUrl: './authenticated.html',
  styleUrl: './authenticated.css',
})
export class Authenticated {}
