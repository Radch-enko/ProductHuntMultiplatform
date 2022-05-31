package com.multi.producthunt.domain.repository.implementation

import com.multi.producthunt.domain.model.ProjectDomain
import com.multi.producthunt.domain.model.toDomain
import com.multi.producthunt.domain.repository.StartupsRepository
import com.multi.producthunt.network.model.ApiResult
import com.multi.producthunt.network.model.body.AddProjectBody
import com.multi.producthunt.network.model.body.CreateCommentBody
import com.multi.producthunt.network.model.body.TopicBody
import com.multi.producthunt.network.model.response.VoteResponse
import com.multi.producthunt.network.service.ProjectsApiService
import com.multi.producthunt.network.util.asCommonFlow
import kotlinx.coroutines.flow.Flow

class StartupsRepositoryImpl(
    private val service: ProjectsApiService
) :
    StartupsRepository {

    override fun addProject(
        name: String,
        tagline: String,
        description: String,
        ownerLink: String,
        thumbnail: String?,
        media: List<String?>,
        topics: List<Int>
    ): Flow<ApiResult<ProjectDomain>> {
        return service.addProject(AddProjectBody(
            name = name,
            tagline = tagline,
            description = description,
            ownerLink = ownerLink,
            thumbnail = thumbnail,
            media = media.filterNotNull(),
            topics = topics.map { TopicBody(it) }
        )).toDomain()
    }

    override fun getProjects(
        cursor: Int,
        pageSize: Int?,
        day: String?
    ): Flow<ApiResult<List<ProjectDomain>>> {
        return if (day == null) {
            service.getProjects(
                cursor = cursor,
                pageSize = pageSize ?: 10
            ).asCommonFlow()
                .toDomain()
        } else {
            service.getProjectsByDay(
                cursor = cursor,
                pageSize = pageSize ?: 10,
                day = day
            ).asCommonFlow()
                .toDomain()
        }
    }

    override fun getProjectById(projectId: Int): Flow<ApiResult<ProjectDomain>> {
        return service.getProjectById(projectId).toDomain()
    }

    override fun commentForProject(
        projectId: Int,
        text: String
    ): Flow<ApiResult<ProjectDomain>> {
        return service.commentForProject(
            CreateCommentBody(projectId, text)
        )
            .toDomain()
    }

    override fun voteProject(projectId: Int): Flow<ApiResult<VoteResponse>> {
        return service.voteForProject(projectId)
    }
}



