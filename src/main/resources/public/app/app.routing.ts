import { ModuleWithProviders }  from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { LoginComponent } from './login.compent'

const appRoutes: Routes = [
    {
        path: 'login',
        component: LoginComponent
     },
    {
        path: '',
        redirectTo: '/login',
        pathMatch: 'full'
    }
];

export const routing: ModuleWithProviders = RouterModule.forRoot(appRoutes);
