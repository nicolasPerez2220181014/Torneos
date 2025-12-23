import { Tournament, TournamentRequest } from '../models/tournament.models';

export class TournamentMapper {
  static mapToDisplay(tournament: Tournament): Tournament {
    return {
      ...tournament,
      startDate: tournament.startDateTime,
      endDate: tournament.endDateTime,
      maxParticipants: tournament.maxFreeCapacity,
      registrationStartDate: tournament.startDateTime,
      registrationEndDate: tournament.endDateTime
    };
  }

  static mapFromForm(formData: any): TournamentRequest {
    return {
      name: formData.name,
      description: formData.description,
      startDateTime: formData.startDate || formData.startDateTime,
      endDateTime: formData.endDate || formData.endDateTime,
      maxFreeCapacity: formData.maxParticipants || formData.maxFreeCapacity || 0,
      isPaid: formData.isPaid || false,
      categoryId: formData.categoryId || '',
      gameTypeId: formData.gameTypeId || ''
    };
  }
}