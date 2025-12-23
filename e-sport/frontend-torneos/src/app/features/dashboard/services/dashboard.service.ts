import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpParams } from '@angular/common/http';
import { HttpBaseService } from '../../../core/services/http-base.service';
import { PaginatedResponse } from '../../../core/models/api.models';
import { AuditLog, AuditFilters, DashboardMetrics } from '../../../core/models/audit.models';

@Injectable({
  providedIn: 'root'
})
export class DashboardService extends HttpBaseService {

  getAuditLogs(page: number = 0, size: number = 20, filters?: AuditFilters): Observable<PaginatedResponse<AuditLog>> {
    let params: any = { page, size };
    
    if (filters) {
      if (filters.userId) params.userId = filters.userId;
      if (filters.action) params.action = filters.action;
      if (filters.entityType) params.entityType = filters.entityType;
      if (filters.startDate) params.startDate = filters.startDate;
      if (filters.endDate) params.endDate = filters.endDate;
    }

    return this.get<PaginatedResponse<AuditLog>>('/audit/logs', new HttpParams({ fromObject: params }));
  }

  getDashboardMetrics(): Observable<DashboardMetrics> {
    return this.get<DashboardMetrics>('/simple/dashboard/metrics');
  }
}