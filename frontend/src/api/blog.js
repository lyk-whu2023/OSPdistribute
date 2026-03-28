import request from '@/utils/request'

/**
 * 获取博客列表
 * @param {Object} params - 查询参数
 * @returns {Promise}
 */
export function getBlogs(params = {}) {
  return request({
    url: '/api/blogs',
    method: 'get',
    params
  })
}

/**
 * 获取博客详情
 * @param {number} id - 博客 ID
 * @returns {Promise}
 */
export function getBlog(id) {
  return request({
    url: `/api/blogs/${id}`,
    method: 'get'
  })
}

/**
 * 创建博客
 * @param {Object} data - 博客信息
 * @returns {Promise}
 */
export function createBlog(data) {
  return request({
    url: '/api/blogs',
    method: 'post',
    data
  })
}

/**
 * 更新博客
 * @param {number} id - 博客 ID
 * @param {Object} data - 博客信息
 * @returns {Promise}
 */
export function updateBlog(id, data) {
  return request({
    url: `/api/blogs/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除博客
 * @param {number} id - 博客 ID
 * @returns {Promise}
 */
export function deleteBlog(id) {
  return request({
    url: `/api/blogs/${id}`,
    method: 'delete'
  })
}

/**
 * 获取博客评论
 * @param {number} blogId - 博客 ID
 * @returns {Promise}
 */
export function getBlogComments(blogId) {
  return request({
    url: `/api/blogs/${blogId}/comments`,
    method: 'get'
  })
}

/**
 * 添加博客评论
 * @param {number} blogId - 博客 ID
 * @param {Object} data - 评论信息
 * @returns {Promise}
 */
export function addBlogComment(blogId, data) {
  return request({
    url: `/api/blogs/${blogId}/comments`,
    method: 'post',
    data
  })
}
