/**
 * 生成随机商品图片 URL
 * 使用 picsum.photos 服务生成随机图片
 * @param {number} width - 图片宽度
 * @param {number} height - 图片高度
 * @param {number|string} seed - 随机种子，可选
 * @returns {string} 图片 URL
 */
export function generateRandomProductImage(width = 400, height = 400, seed = null) {
  const baseUrl = 'https://picsum.photos'
  
  if (seed) {
    return `${baseUrl}/seed/${seed}/${width}/${height}`
  }
  
  return `${baseUrl}/${width}/${height}?random=${Math.random()}`
}

/**
 * 为商品生成多个随机图片
 * @param {number} count - 图片数量
 * @param {number} width - 图片宽度
 * @param {number} height - 图片高度
 * @param {number} productId - 商品 ID，用作种子
 * @returns {Array} 图片 URL 数组
 */
export function generateMultipleProductImages(count = 3, width = 800, height = 800, productId = null) {
  const images = []
  
  for (let i = 0; i < count; i++) {
    const seed = productId ? `${productId}-${i}` : `product-${Date.now()}-${i}`
    images.push({
      imageUrl: generateRandomProductImage(width, height, seed),
      isMain: i === 0
    })
  }
  
  return images
}

/**
 * 根据商品 ID 生成固定的随机图片（保证每次访问相同商品显示相同图片）
 * @param {number} productId - 商品 ID
 * @param {number} width - 图片宽度
 * @param {number} height - 图片高度
 * @returns {string} 图片 URL
 */
export function getProductImageBySeed(productId, width = 400, height = 400) {
  return generateRandomProductImage(width, height, `product-${productId}`)
}

/**
 * 生成分类图片
 * @param {number} categoryId - 分类 ID
 * @param {number} width - 图片宽度
 * @param {number} height - 图片高度
 * @returns {string} 图片 URL
 */
export function getCategoryImage(categoryId, width = 300, height = 200) {
  return generateRandomProductImage(width, height, `category-${categoryId}`)
}

/**
 * 生成用户头像
 * @param {number} userId - 用户 ID
 * @param {number} size - 图片尺寸
 * @returns {string} 头像 URL
 */
export function getUserAvatar(userId, size = 200) {
  return generateRandomProductImage(size, size, `user-${userId}`)
}

/**
 * 生成博客封面图片
 * @param {number} blogId - 博客 ID
 * @param {number} width - 图片宽度
 * @param {number} height - 图片高度
 * @returns {string} 图片 URL
 */
export function generateBlogImage(blogId, width = 400, height = 300) {
  return generateRandomProductImage(width, height, `blog-${blogId}`)
}

/**
 * 生成店铺 Logo 图片
 * @param {number} storeId - 店铺 ID
 * @param {number} width - 图片宽度
 * @param {number} height - 图片高度
 * @returns {string} 图片 URL
 */
export function generateStoreImage(storeId, width = 300, height = 200) {
  return generateRandomProductImage(width, height, `store-${storeId}`)
}
